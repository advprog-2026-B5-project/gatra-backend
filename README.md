# Justifikasi Desain Monitoring

Justifikasi Desain Monitoring
Monitoring diimplementasikan menggunakan Micrometer yang sudah terintegrasi dengan Spring Boot Actuator, dengan visualisasi melalui Grafana Cloud menggunakan plugin Infinity sebagai data source yang mengkonsumsi endpoint /actuator/metrics. Pendekatan ini dipilih karena tidak memerlukan deployment server Prometheus terpisah — Grafana Infinity dapat langsung fetch JSON dari /actuator/metrics/{metricName} tanpa infrastruktur tambahan. Endpoint diamankan dengan custom header X-Grafana-Token melalui ActuatorSecurityFilter.
Dua jenis metric digunakan sesuai karakteristik data. Gauge dipakai untuk nilai real-time dari database, mencakup total jumlah clan aktif (gatra.clan.total), total member aktif di seluruh clan (gatra.clan.members.active.total), jumlah clan per tier Bronze/Silver/Gold/Diamond (gatra.clan.tier.count), dan total aplikasi bergabung yang sedang pending (gatra.clan.membership.pending.total). Counter dipakai untuk akumulasi event, mencakup total clan dibuat (gatra.clan.created.total), dihapus (gatra.clan.deleted.total), aplikasi join (gatra.clan.membership.applied.total), disetujui (gatra.clan.membership.approved.total), ditolak (gatra.clan.membership.rejected.total), member keluar (gatra.clan.membership.left.total), di-kick (gatra.clan.membership.kicked.total), total akses leaderboard (gatra.clan.leaderboard.viewed.total), dan total pergantian musim (gatra.clan.season.reset.total).

Contoh Penggunaan
GET /actuator/metrics/gatra.clan.created.total
Header: X-Grafana-Token: <token>

{
"name": "gatra.clan.created.total",
"description": "Total clan yang berhasil dibuat",
"measurements": [{"statistic": "COUNT", "value": 3.0}]
}

# Justifikasi Proses Profiling
Profiling dilakukan menggunakan IntelliJ IDEA Profiler (async-profiler + JFR) dengan menjalankan app via "Run with Profiler", melakukan semua use case modul melalui frontend (lihat leaderboard semua tier, buat clan, apply/approve membership, season reset), lalu menganalisis Method List dengan filter gatra.
Sebelum optimasi, buildCalculator menghabiskan 705ms, calculateAverageQuizAccuracy 390ms, calculateMissionCompletionRate 315ms, dan ClanResponseMapper.toSimpleResponse 1,140ms. Root cause-nya adalah duplikasi query ke database — calculateMissionCompletionRate dan calculateAverageQuizAccuracy masing-masing memanggil membershipRepository.findByClanIdAndStatus secara terpisah padahal keduanya butuh data yang sama.
Setelah refactor, DB fetch dilakukan 1x saja di buildCalculator dan hasilnya di-pass sebagai List<ClanMembership> ke kedua method. Hasilnya buildCalculator turun dari 705ms → 391ms (-44%), calculateAverageQuizAccuracy dan calculateMissionCompletionRate tidak lagi muncul di top execution list karena hanya melakukan pure stream computation. Efek domino: toSimpleResponse ikut turun dari 1,140ms → 240ms (-79%) karena method ini memanggil buildCalculator di dalamnya.


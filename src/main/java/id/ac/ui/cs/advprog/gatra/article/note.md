## Monitoring

### Justifikasi Desain Monitoring

Implementasi monitoring pada modul bacaan dan kuis dilakukan menggunakan Spring Boot Actuator, Micrometer, Prometheus, dan Grafana. Spring Boot Actuator digunakan untuk mengekspos endpoint observability seperti `/actuator/health`, `/actuator/metrics`, dan `/actuator/prometheus`. Endpoint `/actuator/prometheus` digunakan oleh Prometheus untuk melakukan scraping metric dari aplikasi. 
Micrometer digunakan untuk menambahkan custom business metric yang relevan dengan fitur yang saya kerjakan. Pada modul bacaan, metric yang ditambahkan adalah `monitoring_article_total`, `monitoring_article_viewed_total`, `monitoring_article_marked_read_total`, dan `monitoring_article_deleted_total`. Metric tersebut digunakan untuk memantau jumlah artikel yang dibuat, dibuka, ditandai sudah dibaca, dan dihapus.
Pada modul kuis, metric yang digunakan adalah `monitoring_question_total`, `monitoring_question_updated_total`, `monitoring_question_deleted_total`, `monitoring_passing_score_updated_total`, `monitoring_quiz_submitted_total`, `monitoring_quiz_passed_total`, dan `monitoring_quiz_failed_total`. Metric tersebut digunakan untuk memantau aktivitas pembuatan soal, perubahan soal, penghapusan soal, perubahan passing score, jumlah submit kuis, serta hasil kuis lulus atau gagal.
Desain ini dipilih karena monitoring tidak hanya mencakup aspek teknis aplikasi seperti uptime, CPU usage, memory usage, response time, dan jumlah HTTP request, tetapi juga mencakup aktivitas bisnis utama pada modul bacaan dan kuis. Dengan adanya custom metric, kita dapat mengetahui apakah fitur utama digunakan dengan normal, mendeteksi peningkatan aktivitas pengguna, serta melihat potensi masalah seperti banyaknya quiz yang gagal atau endpoint yang lambat.

### Contoh Penggunaan
Monitoring digunakan dengan menjalankan aplikasi Spring Boot, Prometheus, dan Grafana. Setelah aplikasi berjalan, endpoint pada modul bacaan dan kuis diakses untuk menghasilkan metric, misalnya:

- `POST /api/articles`
- `GET /api/articles/{id}`
- `POST /api/articles/{id}/read`

Setelah endpoint tersebut diakses, Prometheus digunakan untuk mengecek metric custom. Contoh hasil monitoring yang berhasil diperoleh adalah:
![img_1.png](img_1.png)
![img_2.png](img_2.png)

Metric tersebut membuktikan bahwa aktivitas pada modul bacaan dan kuis berhasil tercatat. Selain itu, Grafana digunakan untuk memvisualisasikan metric dalam bentuk dashboard yang lebih mudah dibaca.
![img.png](img.png)

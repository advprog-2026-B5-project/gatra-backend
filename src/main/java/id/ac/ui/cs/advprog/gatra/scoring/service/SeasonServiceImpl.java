package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.auth.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeasonServiceImpl implements SeasonService {

    private final StudentProfileRepository studentProfileRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    @Transactional
    public void resetSeason() {
        // Reset poin Student (menjadi 0)
        studentProfileRepository.resetAllStudentPoints();

        // Bersihkan Point History
        pointHistoryRepository.deleteAllInBatch();
    }
}
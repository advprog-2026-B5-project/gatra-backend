package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.auth.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeasonServiceImpl implements SeasonService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Override
    @Transactional
    public void resetSeason() {
        // Reset poin Student (menjadi 0)
        studentProfileRepository.resetAllStudentPoints();

        // Bersihkan Point History
        pointHistoryRepository.deleteAllInBatch();
    }
}
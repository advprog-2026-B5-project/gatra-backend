package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SeasonServiceImplTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private SeasonServiceImpl seasonService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testResetSeason_ShouldCallAllRepositoryResetMethods() {
        // Act
        seasonService.resetSeason();

        // Assert
        verify(studentProfileRepository, times(1)).resetAllStudentPoints();
        verify(pointHistoryRepository, times(1)).deleteAllInBatch();
    }
}
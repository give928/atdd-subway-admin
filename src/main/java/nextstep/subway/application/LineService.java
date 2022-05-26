package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineSaveRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse save(LineSaveRequest lineSaveRequest) {
        Station upStation = findStation(lineSaveRequest.getUpStationId());
        Station downStation = findStation(lineSaveRequest.getDownStationId());

        Line persistLine = lineRepository.save(lineSaveRequest.toLine(upStation, downStation));

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        persistLine.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = findStation(sectionRequest.getUpStationId());
        Station downStation = findStation(sectionRequest.getDownStationId());
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());
        Line line = lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        line.addSection(section);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void removeSection(Long id, Long stationId) {
        Station station = findStation(stationId);
        Line line = lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        line.removeSection(station);
    }
}

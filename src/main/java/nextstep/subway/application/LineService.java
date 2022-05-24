package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineSaveRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
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
        Station upStation = stationRepository.findById(lineSaveRequest.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(lineSaveRequest.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);

        Line persistLine = lineRepository.save(lineSaveRequest.toLine(upStation, downStation));

        return new LineResponse(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return new LineResponse(line);
    }

    @Transactional
    public LineResponse update(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Line persistLine = line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());

        return new LineResponse(persistLine);
    }

    @Transactional
    public void delete(Long id) {
        lineRepository.deleteById(id);
    }
}

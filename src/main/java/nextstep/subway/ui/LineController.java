package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineSaveRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineSaveRequest lineSaveRequest) {
        LineResponse line = lineService.save(lineSaveRequest);
        return ResponseEntity.created(URI.create(String.format("/lines/%d", line.getId()))).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.update(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleRuntimeException() {
        return ResponseEntity.badRequest().build();
    }
}

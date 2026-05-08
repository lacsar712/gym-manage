package com.gymmanage.coach;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymmanage.coach.dto.CoachSaveRequest;
import com.gymmanage.coach.entity.Coach;
import com.gymmanage.coach.service.CoachService;
import com.gymmanage.common.ApiResponse;
import com.gymmanage.common.BizException;
import com.gymmanage.common.EnumNormalizer;
import com.gymmanage.common.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {
    private final CoachService coachService;

    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    @PostMapping
    public ApiResponse<Coach> create(@Valid @RequestBody CoachSaveRequest req) {
        Coach c = new Coach();
        c.setName(req.getName().trim());
        c.setPhone(req.getPhone().trim());
        c.setSpecialty(req.getSpecialty());
        c.setStatus(EnumNormalizer.activeStatus(req.getStatus(), "status"));
        c.setRemark(req.getRemark());
        coachService.save(c);
        return ApiResponse.ok(c);
    }

    @PutMapping("/{id}")
    public ApiResponse<Coach> update(@PathVariable Long id, @Valid @RequestBody CoachSaveRequest req) {
        Coach existing = coachService.getById(id);
        if (existing == null) {
            throw BizException.notFound("coach not found");
        }
        existing.setName(req.getName().trim());
        existing.setPhone(req.getPhone().trim());
        existing.setSpecialty(req.getSpecialty());
        existing.setStatus(EnumNormalizer.activeStatus(req.getStatus(), "status"));
        existing.setRemark(req.getRemark());
        coachService.updateById(existing);
        return ApiResponse.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        coachService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse<PageResult<Coach>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<Coach> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Coach> qw = Wrappers.lambdaQuery(Coach.class);
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            qw.and(w -> w.like(Coach::getName, kw).or().like(Coach::getPhone, kw));
        }
        qw.orderByDesc(Coach::getId);
        coachService.page(p, qw);
        return ApiResponse.ok(PageResult.from(p));
    }
}


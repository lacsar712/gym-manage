package com.gymmanage.course;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymmanage.coach.entity.Coach;
import com.gymmanage.coach.service.CoachService;
import com.gymmanage.common.ApiResponse;
import com.gymmanage.common.BizException;
import com.gymmanage.common.EnumNormalizer;
import com.gymmanage.common.PageResult;
import com.gymmanage.course.dto.CourseSaveRequest;
import com.gymmanage.course.entity.Course;
import com.gymmanage.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;
    private final CoachService coachService;

    public CourseController(CourseService courseService, CoachService coachService) {
        this.courseService = courseService;
        this.coachService = coachService;
    }

    @PostMapping
    public ApiResponse<Course> create(@Valid @RequestBody CourseSaveRequest req) {
        validateCoach(req.getCoachId());
        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw BizException.badRequest("endTime must be after startTime");
        }

        Course c = new Course();
        c.setTitle(req.getTitle().trim());
        c.setCoachId(req.getCoachId());
        c.setStartTime(req.getStartTime());
        c.setEndTime(req.getEndTime());
        c.setCapacity(req.getCapacity());
        c.setStatus(EnumNormalizer.courseStatus(req.getStatus()));
        c.setRemark(req.getRemark());
        courseService.save(c);
        return ApiResponse.ok(c);
    }

    @PutMapping("/{id}")
    public ApiResponse<Course> update(@PathVariable Long id, @Valid @RequestBody CourseSaveRequest req) {
        Course existing = courseService.getById(id);
        if (existing == null) {
            throw BizException.notFound("course not found");
        }
        validateCoach(req.getCoachId());
        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw BizException.badRequest("endTime must be after startTime");
        }

        existing.setTitle(req.getTitle().trim());
        existing.setCoachId(req.getCoachId());
        existing.setStartTime(req.getStartTime());
        existing.setEndTime(req.getEndTime());
        existing.setCapacity(req.getCapacity());
        existing.setStatus(EnumNormalizer.courseStatus(req.getStatus()));
        existing.setRemark(req.getRemark());
        courseService.updateById(existing);
        return ApiResponse.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse<PageResult<Course>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long coachId
    ) {
        Page<Course> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Course> qw = Wrappers.lambdaQuery(Course.class);
        if (keyword != null && !keyword.isBlank()) {
            qw.like(Course::getTitle, keyword.trim());
        }
        if (coachId != null) {
            qw.eq(Course::getCoachId, coachId);
        }
        qw.orderByDesc(Course::getId);
        courseService.page(p, qw);
        return ApiResponse.ok(PageResult.from(p));
    }

    private void validateCoach(Long coachId) {
        Coach coach = coachService.getById(coachId);
        if (coach == null) {
            throw BizException.badRequest("coachId not exists");
        }
        if (!"ACTIVE".equalsIgnoreCase(coach.getStatus())) {
            throw BizException.badRequest("coach must be ACTIVE");
        }
    }
}


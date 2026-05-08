package com.gymmanage.checkin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymmanage.checkin.dto.CheckinSaveRequest;
import com.gymmanage.checkin.entity.MemberCheckin;
import com.gymmanage.checkin.service.MemberCheckinService;
import com.gymmanage.common.ApiResponse;
import com.gymmanage.common.BizException;
import com.gymmanage.common.PageResult;
import com.gymmanage.member.entity.Member;
import com.gymmanage.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/checkins")
public class CheckinController {
    private final MemberCheckinService memberCheckinService;
    private final MemberService memberService;

    public CheckinController(MemberCheckinService memberCheckinService, MemberService memberService) {
        this.memberCheckinService = memberCheckinService;
        this.memberService = memberService;
    }

    @PostMapping
    public ApiResponse<MemberCheckin> create(@Valid @RequestBody CheckinSaveRequest req) {
        Member m = memberService.getById(req.getMemberId());
        if (m == null) {
            throw BizException.badRequest("memberId not exists");
        }
        MemberCheckin c = new MemberCheckin();
        c.setMemberId(req.getMemberId());
        c.setMemberName(m.getName());
        c.setCheckinTime(LocalDateTime.now());
        c.setRemark(req.getRemark());
        memberCheckinService.save(c);
        return ApiResponse.ok(c);
    }

    @GetMapping
    public ApiResponse<PageResult<MemberCheckin>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo
    ) {
        Page<MemberCheckin> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<MemberCheckin> qw = Wrappers.lambdaQuery(MemberCheckin.class);
        if (memberId != null) {
            qw.eq(MemberCheckin::getMemberId, memberId);
        }
        if (dateFrom != null) {
            qw.ge(MemberCheckin::getCheckinTime, dateFrom.atStartOfDay());
        }
        if (dateTo != null) {
            qw.lt(MemberCheckin::getCheckinTime, dateTo.plusDays(1).atStartOfDay());
        }
        qw.orderByDesc(MemberCheckin::getId);
        memberCheckinService.page(p, qw);
        return ApiResponse.ok(PageResult.from(p));
    }
}


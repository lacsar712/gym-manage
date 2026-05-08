package com.gymmanage.member;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymmanage.common.ApiResponse;
import com.gymmanage.common.BizException;
import com.gymmanage.common.EnumNormalizer;
import com.gymmanage.common.PageResult;
import com.gymmanage.member.dto.MemberSaveRequest;
import com.gymmanage.member.entity.Member;
import com.gymmanage.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ApiResponse<Member> create(@Valid @RequestBody MemberSaveRequest req) {
        Member m = new Member();
        m.setName(req.getName().trim());
        m.setPhone(req.getPhone().trim());
        m.setGender(EnumNormalizer.memberGender(req.getGender()));
        m.setJoinDate(req.getJoinDate());
        m.setStatus(EnumNormalizer.activeStatus(req.getStatus(), "status"));
        m.setRemark(req.getRemark());
        memberService.save(m);
        return ApiResponse.ok(m);
    }

    @PutMapping("/{id}")
    public ApiResponse<Member> update(@PathVariable Long id, @Valid @RequestBody MemberSaveRequest req) {
        Member existing = memberService.getById(id);
        if (existing == null) {
            throw BizException.notFound("member not found");
        }
        existing.setName(req.getName().trim());
        existing.setPhone(req.getPhone().trim());
        existing.setGender(EnumNormalizer.memberGender(req.getGender()));
        existing.setJoinDate(req.getJoinDate());
        existing.setStatus(EnumNormalizer.activeStatus(req.getStatus(), "status"));
        existing.setRemark(req.getRemark());
        memberService.updateById(existing);
        return ApiResponse.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        memberService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse<PageResult<Member>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<Member> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Member> qw = Wrappers.lambdaQuery(Member.class);
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            qw.and(w -> w.like(Member::getName, kw).or().like(Member::getPhone, kw));
        }
        qw.orderByDesc(Member::getId);
        memberService.page(p, qw);
        return ApiResponse.ok(PageResult.from(p));
    }
}


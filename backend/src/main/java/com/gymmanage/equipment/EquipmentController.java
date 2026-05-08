package com.gymmanage.equipment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymmanage.common.ApiResponse;
import com.gymmanage.common.BizException;
import com.gymmanage.common.EnumNormalizer;
import com.gymmanage.common.PageResult;
import com.gymmanage.equipment.dto.EquipmentSaveRequest;
import com.gymmanage.equipment.entity.Equipment;
import com.gymmanage.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public ApiResponse<Equipment> create(@Valid @RequestBody EquipmentSaveRequest req) {
        Equipment e = new Equipment();
        e.setName(req.getName().trim());
        e.setCode(normalizeCode(req.getCode()));
        e.setLocation(req.getLocation());
        e.setStatus(EnumNormalizer.equipmentStatus(req.getStatus()));
        e.setBuyDate(req.getBuyDate());
        e.setRemark(req.getRemark());
        equipmentService.save(e);
        return ApiResponse.ok(e);
    }

    @PutMapping("/{id}")
    public ApiResponse<Equipment> update(@PathVariable Long id, @Valid @RequestBody EquipmentSaveRequest req) {
        Equipment existing = equipmentService.getById(id);
        if (existing == null) {
            throw BizException.notFound("equipment not found");
        }
        existing.setName(req.getName().trim());
        existing.setCode(normalizeCode(req.getCode()));
        existing.setLocation(req.getLocation());
        existing.setStatus(EnumNormalizer.equipmentStatus(req.getStatus()));
        existing.setBuyDate(req.getBuyDate());
        existing.setRemark(req.getRemark());
        equipmentService.updateById(existing);
        return ApiResponse.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        equipmentService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse<PageResult<Equipment>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        Page<Equipment> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Equipment> qw = Wrappers.lambdaQuery(Equipment.class);
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            qw.and(w -> w.like(Equipment::getName, kw).or().like(Equipment::getCode, kw));
        }
        if (status != null && !status.isBlank()) {
            qw.eq(Equipment::getStatus, EnumNormalizer.equipmentStatus(status));
        }
        qw.orderByDesc(Equipment::getId);
        equipmentService.page(p, qw);
        return ApiResponse.ok(PageResult.from(p));
    }

    private static String normalizeCode(String raw) {
        if (raw == null) {
            return null;
        }
        String v = raw.trim();
        if (v.isEmpty()) {
            return null;
        }
        return v;
    }
}


package com.gymmanage.equipment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymmanage.equipment.entity.Equipment;
import com.gymmanage.equipment.mapper.EquipmentMapper;
import com.gymmanage.equipment.service.EquipmentService;
import org.springframework.stereotype.Service;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {
}


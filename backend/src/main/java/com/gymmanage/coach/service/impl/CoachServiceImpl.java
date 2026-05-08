package com.gymmanage.coach.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymmanage.coach.entity.Coach;
import com.gymmanage.coach.mapper.CoachMapper;
import com.gymmanage.coach.service.CoachService;
import org.springframework.stereotype.Service;

@Service
public class CoachServiceImpl extends ServiceImpl<CoachMapper, Coach> implements CoachService {
}


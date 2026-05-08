package com.gymmanage.checkin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymmanage.checkin.entity.MemberCheckin;
import com.gymmanage.checkin.mapper.MemberCheckinMapper;
import com.gymmanage.checkin.service.MemberCheckinService;
import org.springframework.stereotype.Service;

@Service
public class MemberCheckinServiceImpl extends ServiceImpl<MemberCheckinMapper, MemberCheckin> implements MemberCheckinService {
}


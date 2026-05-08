package com.gymmanage.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymmanage.member.entity.Member;
import com.gymmanage.member.mapper.MemberMapper;
import com.gymmanage.member.service.MemberService;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
}


package com.gymmanage.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymmanage.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}


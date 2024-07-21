package com.dnd.zzaekkac.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1385560276L;

    public static final QMember member = new QMember("member1");

    public final BooleanPath isDelete = createBoolean("isDelete");

    public final NumberPath<Long> kakaoId = createNumber("kakaoId", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath memberNickname = createString("memberNickname");

    public final StringPath memberProfile = createString("memberProfile");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}


package xyz.tumit.spbjwtdemo.member.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member {
    private Long id;
    private String name;
}

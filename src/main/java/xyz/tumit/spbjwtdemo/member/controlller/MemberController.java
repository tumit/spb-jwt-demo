package xyz.tumit.spbjwtdemo.member.controlller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.tumit.spbjwtdemo.member.model.Member;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    @GetMapping()
    public List<Member> getCustomers() {
        return Arrays.asList(
                Member.builder().id(1L).name("cherprang").build(),
                Member.builder().id(2L).name("jennis").build()
        );
    }

}

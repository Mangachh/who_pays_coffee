package cbs.wantACoffe.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateNickname {
    private Long groupId;
    private String oldNickname;
    private String newNickname;
}

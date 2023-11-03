package cbs.wantACoffe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberGroup {
    
    private Long groupId;
    private String nickname;
    private String username;
    
    @Default
    private boolean isAdmin = false;
}

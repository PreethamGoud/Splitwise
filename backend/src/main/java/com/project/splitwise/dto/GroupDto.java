package com.project.splitwise.dto;

import com.project.splitwise.entity.Expense;
import com.project.splitwise.entity.Group;
import com.project.splitwise.entity.UserGroup;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String groupName;
    private String description;
    private List<String> userNames = new ArrayList<>();

    public GroupDto(Group inputGroup){
        this.id = inputGroup.getId();
        this.groupName = inputGroup.getGroupName();
        this.description = inputGroup.getDescription();

        for(UserGroup i : inputGroup.getUsers()) this.userNames.add(i.getUser().getUserName());
    }
}

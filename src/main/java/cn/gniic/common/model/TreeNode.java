package cn.gniic.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 树形节点
 * </p>
 *
 * @author Caratacus
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TreeNode extends BaseModel {

    protected Integer parentId;

    protected List<TreeNode> childrens = new ArrayList<>();
}

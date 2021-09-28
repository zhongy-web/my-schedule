
import java.util.*;

public class MyTest {
    public static void main(String[] args) {
        MyTest myTest = new MyTest();
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(2);
        TreeNode treeNode3 = new TreeNode(3);
        TreeNode treeNode4 = new TreeNode(4);
        TreeNode treeNode5 = new TreeNode(5);
        treeNode1.left = treeNode2;
        treeNode1.right = treeNode3;
        treeNode2.left = treeNode4;
        treeNode2.right = treeNode5;
        System.out.println(myTest.levelOrder(treeNode1));
    }
    public static class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
    }
    public int[] levelOrder(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        List<Integer> list = new ArrayList<>();
        if(root == null) return new int[]{};
        queue.add(root);
        if(!queue.isEmpty()) {
                TreeNode node = queue.poll();
                list.add(node.val);
                if(node.left != null) queue.add(node.left);
                if(node.right != null) queue.add(node.right);
        }
        int[] res = new int[list.size()];
        for(int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }
}

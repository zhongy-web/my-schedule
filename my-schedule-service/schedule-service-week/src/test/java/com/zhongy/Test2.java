package com.zhongy;

import org.junit.Test;

import java.util.*;

public class Test2 {
    public static void main(String[] args) {
        firstUniqChar("loveleetcode");
    }

    static class Solution {
        boolean[][] visited;
        int m;
        int n;
        int[][] matrix;
        List<Integer> path;
        public int[] spiralOrder(int[][] matrix) {
            this.m = matrix.length;
            this.n = matrix[0].length;
            this.matrix = matrix;
            this.visited = new boolean[m][n];
            this.path = new ArrayList<Integer>();
            int[] res = new int[m * n];
            dfs(0, 0, path);
            for(int i = 0; i < path.size(); i++) {
                res[i] = path.get(i);
            }
            return res;
        }

        public void dfs(int i, int j, List<Integer> path) {
            if((i < 0 || i >= m) || (j < 0 || j >= n) || visited[i][j]) return;
            visited[i][j] = true;
            path.add(matrix[i][j]);
            if(j < n && !visited[i][j]) dfs(i, j + 1, path);
            if (i < m && !visited[i][j]) dfs(i + 1, j, path);
            if (j >= 0 && !visited[i][j]) dfs(i, j - 1, path);
            if (i >= 0 && !visited[i][j])dfs(i - 1, j, path);
        }
    }
    public static char firstUniqChar(String s) {
        Map<Character,Integer> map = new HashMap<>();
        char[] chars = s.toCharArray();
        for(char c : chars) {
            if(map.containsKey(c)) {
                map.put(c, 2);
            }
            map.put(c, 1);
        }
        for(char c : chars) {
            //找到了
            if(map.get(c) == 1) {
                return c;
            }
        }
        return ' ';
    }
}
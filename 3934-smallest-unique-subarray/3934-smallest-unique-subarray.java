
import java.util.*;

class Solution {

    public int smallestUniqueSubarray(int[] nums) {

        int n = nums.length;

        // Step 1: Build Suffix Array
        int[] sa = buildSuffixArray(nums);

        // Step 2: Build Rank Array
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        // Step 3: Build LCP Array using Kasai's Algorithm
        int[] lcp = new int[n];

        int k = 0;

        for (int i = 0; i < n; i++) {

            int r = rank[i];

            if (r == 0) {
                k = 0;
                continue;
            }

            int j = sa[r - 1];

            while (i + k < n &&
                   j + k < n &&
                   nums[i + k] == nums[j + k]) {
                k++;
            }

            lcp[r] = k;

            if (k > 0) {
                k--;
            }
        }

        // Step 4: Find Minimum Unique Subarray Length
        int ans = n;

        for (int r = 0; r < n; r++) {

            int maxLCP = lcp[r];

            if (r + 1 < n) {
                maxLCP = Math.max(maxLCP, lcp[r + 1]);
            }

            int uniqueLength = maxLCP + 1;

            // Check if enough elements are available
            int remainingLength = n - sa[r];

            if (uniqueLength <= remainingLength) {
                ans = Math.min(ans, uniqueLength);
            }
        }

        return ans;
    }

    // Build Suffix Array using Doubling + Counting Sort
    private int[] buildSuffixArray(int[] nums) {

        int n = nums.length;
        int N = n + 1;

        int[] s = new int[N];

        for (int i = 0; i < n; i++) {
            s[i] = nums[i] + 1;
        }

        // Sentinel (smallest element)
        s[n] = 0;

        int[] p = new int[N];
        int[] c = new int[N];

        int[] cnt = new int[Math.max(100002, N)];

        // Initial Sorting
        for (int x : s) {
            cnt[x]++;
        }

        for (int i = 1; i < cnt.length; i++) {
            cnt[i] += cnt[i - 1];
        }

        for (int i = N - 1; i >= 0; i--) {
            p[--cnt[s[i]]] = i;
        }

        // Initial Equivalence Classes
        c[p[0]] = 0;

        int classes = 1;

        for (int i = 1; i < N; i++) {

            if (s[p[i]] != s[p[i - 1]]) {
                classes++;
            }

            c[p[i]] = classes - 1;
        }

        int[] pn = new int[N];
        int[] cn = new int[N];

        // Doubling Algorithm
        for (int len = 1; len < N; len *= 2) {

            for (int i = 0; i < N; i++) {

                pn[i] = p[i] - len;

                if (pn[i] < 0) {
                    pn[i] += N;
                }
            }

            Arrays.fill(cnt, 0);

            for (int i = 0; i < N; i++) {
                cnt[c[pn[i]]]++;
            }

            for (int i = 1; i < classes; i++) {
                cnt[i] += cnt[i - 1];
            }

            for (int i = N - 1; i >= 0; i--) {

                int x = pn[i];

                p[--cnt[c[x]]] = x;
            }

            cn[p[0]] = 0;

            int newClasses = 1;

            for (int i = 1; i < N; i++) {

                int cur = p[i];
                int prev = p[i - 1];

                int midCur = (cur + len) % N;
                int midPrev = (prev + len) % N;

                if (c[cur] != c[prev] ||
                    c[midCur] != c[midPrev]) {

                    newClasses++;
                }

                cn[cur] = newClasses - 1;
            }

            c = cn.clone();
            classes = newClasses;

            if (classes == N) {
                break;
            }
        }

        // Remove Sentinel Suffix
        int[] sa = new int[n];

        for (int i = 0; i < n; i++) {
            sa[i] = p[i + 1];
        }

        return sa;
    }
}

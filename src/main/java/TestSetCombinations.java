public class TestSetCombinations {

	    public static void main(String[] args) {
	        String[] set1 = {"lala","foo"};
	        Double[] set2 = {4.0,2.0,1.0};
	        //Double[] set3 = {3.0, 2.0, 1.0, 5.0};
	        //Double[] set4 = {1.0,1.0};
	        Object[][] sets = {set1, set2};//, set3, set4};

	        Object[][] combinations = getCombinations(sets);

	        for (int i = 0; i < combinations.length; i++) {
	            for (int j = 0; j < combinations[0].length; j++) {
	              System.out.print(combinations[i][j]+" ");
	            }
	            System.out.println();
	        }
	    }

	    private static Object[][] getCombinations(Object[][] sets) {

	      int[] counters = new int[sets.length];
	        int count = 1;   
	        int count2 = 0;

	        for (int i = 0; i < sets.length; i++) {
	          count *= sets[i].length;
	        }

	        Object[][] combinations = new Object[count][sets.length];

	        do{
	           combinations[count2++] = getCombinationString(counters, sets);
	        } while(increment(counters, sets));

	        return combinations;
	    }

	    private static Object[] getCombinationString(int[] counters, Object[][] sets) {

	      Object[] o = new Object[counters.length];
	      for(int i = 0; i<counters.length;i++) {
	        o[i] = sets[i][counters[i]];
	      }
	      return o;

	    }

	    private static boolean increment(int[] counters, Object[][] sets) {
	        for(int i=counters.length-1;i>=0;i--) {
	            if(counters[i] < sets[i].length-1) {
	                counters[i]++;
	                return true;
	            } else {
	                counters[i] = 0;
	            }
	        }
	        return false;
	    }

}

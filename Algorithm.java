package mlc.procedures;

import java.io.File;

import mlc.components.*;

public class Algorithm {
	//Variables used for the class.
		Graph g;
		
		public Algorithm(File source) throws Exception {
			g = new Graph(source);
		}
}

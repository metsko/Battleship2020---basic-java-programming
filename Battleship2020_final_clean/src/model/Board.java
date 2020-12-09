package model;

public class Board {
	public static final int[] minDim = new int[] { 4, 4 };

	// 0,1,2,3,4,5,6,,7,8,9 (row- & columnindices)
	public static final int[] defaultDim = new int[] { 9, 9 };
	private int[] dim;

	public Board() {
		// if user did not specify dimensions,
		// defaultDim is applied.
		this.dim = defaultDim;
	}

	public int[] getDim() {
		return dim;
	}

	public void setDim(int[] dim) {
		this.dim = dim;
	}

	public void setDimX(int dimX) {
		this.dim[0] = dimX;
	}

	public void setDimY(int dimY) {
		this.dim[1] = dimY;
	}
}

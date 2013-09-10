package world;

public class Point implements Comparable<Point> {

	public final int x, z;

	public Point(int x, int y) {
		this.x = x;
		this.z = y;
	}

	@Override
	public int compareTo(Point that) {
		if (this.x == that.x && this.z == that.z) {
			return 0;
		}

		if (this.x == that.x) {
			if (this.z < that.z) {
				return -1;
			}

			return +1;
		}

		if (this.x < that.x) {
			return -1;
		}

		return 1;

	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Point) {
			Point thatAgain = (Point) that;
			return this.x == thatAgain.x && this.z == thatAgain.z;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return x * 31 + z;
	}
}

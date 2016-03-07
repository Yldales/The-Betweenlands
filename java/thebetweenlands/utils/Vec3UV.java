package thebetweenlands.utils;

import net.minecraft.util.IIcon;

public class Vec3UV {
	public double x, y, z, u, v, uw, vw;
	public Vec3UV(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = 0.0D;
		this.v = 0.0D;
		this.uw = 1.0D;
		this.vw = 1.0D;
	}
	public Vec3UV(double x, double y, double z, double u, double v, double uw, double vw) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = u;
		this.v = v;
		this.uw = uw;
		this.vw = vw;
	}
	public Vec3UV(Vec3UV vec, double u, double v, double uw, double vw) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.u = u;
		this.v = v;
		this.uw = uw;
		this.vw = vw;
	}
	public Vec3UV(double u, double v, double uw, double vw) {
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
		this.u = u;
		this.v = v;
		this.uw = uw;
		this.vw = vw;
	}
	public Vec3UV(Vec3UV vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		this.u = vec.u;
		this.v = vec.v;
		this.uw = vec.uw;
		this.vw = vec.vw;
	}
	public Vec3UV cross(Vec3UV vec) {
		Vec3UV crossProduct = new Vec3UV(0, 0, 0);
		crossProduct.x = this.y * vec.z - vec.y * this.z;
		crossProduct.y = this.z * vec.x - vec.z * this.x;
		crossProduct.z = this.x * vec.y - vec.x * this.y;
		return crossProduct;
	}
	public Vec3UV neg() {
		return new Vec3UV(-this.x, -this.y, -this.z);
	}
	public double getU(IIcon icon, int width) {
		double umin = icon.getMinU();
		double umax = icon.getMaxU();
		return umin + (umax - umin) * this.uw / (double)width * this.u;
	}
	public double getV(IIcon icon, int height) {
		double vmin = icon.getMinV();
		double vmax = icon.getMaxV();
		return vmin + (vmax - vmin) * this.vw / (double)height * this.v;
	}
}
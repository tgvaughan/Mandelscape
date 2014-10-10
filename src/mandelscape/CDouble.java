/*
 * Copyright (C) 2014 Tim Vaughan <tgvaughan@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mandelscape;

/**
 * Basic complex number class.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class CDouble {

    public double real, imag;

    /**
     * Origin of the complex plane.
     */
    public static CDouble ZERO = new CDouble(0, 0);

    /**
     * One as a complex number.
     */
    public static CDouble ONE = new CDouble(1, 0);

    /**
     * The imaginary unit.
     */
    public static CDouble J = new CDouble(0, 1);

    /**
     * Create a new complex number with the given real and imaginary
     * parts.
     * 
     * @param real
     * @param imag 
     */
    public CDouble(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * Add two complex numbers together.
     * 
     * @param arg
     * @return new complex number
     */
    public CDouble add(CDouble arg) {
        return new CDouble(real + arg.real, imag + arg.imag);
    }

    /**
     * Multiply two complex numbers together.
     * 
     * @param arg
     * @return new complex number
     */
    public CDouble mul(CDouble arg) {
        return new CDouble(real*arg.real - imag*arg.imag,
            imag*arg.real + real*arg.imag);
    }

    /**
     * Square this complex number.
     * 
     * @return new complex number
     */
    public CDouble squared() {
        return mul(this);
    }

    /**
     * Scale this complex number by a real factor.
     * 
     * @param arg
     * @return new complex number
     */
    public CDouble scale(double arg) {
        return new CDouble(arg*real, arg*imag);
    }

    /**
     * Return the square of the absolute value of this complex number.
     * 
     * @return result
     */
    public double abs2() {
        return real*real + imag*imag;
    }

    /**
     * Return the absolute value of this complex number.
     * 
     * @return result
     */
    public double abs() {
        return Math.sqrt(abs2());
    }

    /**
     * Test to see whether two complex numbers are equal.
     * 
     * @param obj
     * @return True if both the real and imaginary components are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CDouble) {
            CDouble other = (CDouble)obj;
            return other.real == real && other.imag == imag;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.real) ^ (Double.doubleToLongBits(this.real) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.imag) ^ (Double.doubleToLongBits(this.imag) >>> 32));
        return hash;
    }

    
}

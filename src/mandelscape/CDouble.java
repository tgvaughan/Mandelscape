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

    public static CDouble ZERO = new CDouble(0, 0);
    public static CDouble ONE = new CDouble(1, 0);
    public static CDouble J = new CDouble(0, 1);

    public CDouble(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public CDouble add(CDouble arg) {
        return new CDouble(real + arg.real, imag + arg.imag);
    }

    public CDouble mul(CDouble arg) {
        return new CDouble(real*arg.real - imag*arg.imag,
            imag*arg.real + real*arg.imag);
    }

    public CDouble squared() {
        return mul(this);
    }

    public CDouble scale(double arg) {
        return new CDouble(arg*real, arg*imag);
    }

    public double abs2() {
        return real*real + imag*imag;
    }

    public double abs() {
        return Math.sqrt(abs());
    }

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

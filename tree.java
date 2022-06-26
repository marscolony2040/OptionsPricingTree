import java.util.*;
import java.text.*;
import java.io.*;
import java.math.*;


class tree {

    public static void main(String[] args){
        double stock, strike, rf, vol, mat;
        int nodes;
        String optype;

        stock = 100;
        strike = 95;
        rf = 0.03;
        vol = 0.21;
        mat = 3.0/12.0;
        optype = "Put";
        nodes = 8;

        int r, c;
        double u, d, pu, pd;
        r = Row(nodes);
        c = Col(nodes);

        u = up(vol, mat, nodes);
        d = down(vol, mat, nodes);
        pu = Pup(rf, mat, u, d);
        pd = Pdown(pu);

        double[][] data = new double[r][c];
        data = Forward(data, stock, u, d);
        data = Solve(data, strike, optype);
        data = Backward(data, pu, pd, optype);


        PrintM(data);

    }

    public static double[][] B1(int d_end, int r_start, int c_start, double[][] x, double u, double d, String optype)
    {
        while(r_start < d_end){
            if(optype == "Call"){
                x[r_start][c_start] = Math.max(u*x[r_start - 2][c_start + 1] + d*x[r_start + 2][c_start + 1], 0.0);
            } else {
                x[r_start][c_start] = Math.max(d*x[r_start - 2][c_start + 1] + u*x[r_start + 2][c_start + 1], 0.0);
            }
            r_start += 4;
        }

        return x;
    }

    public static double[][] Backward(double[][] x, double u, double d, String optype){
        int r, c, r_start, d_end;
        r = x.length;
        c = x[0].length;
        r_start = 3;
        d_end = x.length;

        /*
        x = B1(d_end, r_start, c - 2, x, u, d);
        x = B1(d_end-2, r_start + 2, c - 3, x, u, d);
        x = B1(d_end-4, r_start + 4, c - 4, x, u, d);
        x = B1(d_end-6, r_start + 6, c - 5, x, u, d);
        x = B1(d_end-8, r_start + 8, c - 6, x, u, d);
        x = B1(d_end-10, r_start + 10, c - 7, x, u, d);
        x = B1(d_end-12, r_start + 12, c - 8, x, u, d);
         */

        int ct = 0;
        int dt = 0;

        for(int kk = 0; kk < c - 1; kk++){
            ct = 2*kk;
            dt = kk + 2;
            x = B1(d_end-ct, r_start + ct, c - dt, x, u, d, optype);
        }


        return x;
    }

    public static double[][] Solve(double[][] x, double k, String optype){
        int r = x.length;
        int c = x[0].length;
        int p = 0;

        while(p < r){
            if(optype == "Call"){
                x[p+1][c - 1] = Math.max(x[p][c-1] - k, 0.0);
            } else {
                x[p+1][c - 1] = Math.max(k - x[p][c-1], 0.0);
            }
            p += 4;
        }

        return x;
    }

    public static double[][] F1(int ctr, int col, double[][] x, double s, double u, double d) {
        int pup = 0;
        for(int i = col; i < x[0].length; i++){
            x[ctr - pup][i] = s*Math.pow(u, i-col);
            x[ctr + pup][i] = s*Math.pow(d, i-col);
            pup += 2;
        }

        return x;
    }

    public static double[][] Forward(double[][] x, double s, double u, double d)
    {
        int ctr = (x.length/2) - 1;

        int skip;
        skip = 0;

        while(skip <= x[0].length){
            x = F1(ctr, skip, x, s, u, d);
            skip += 2;
        }


        return x;
    }

    public static double Pdown(double up){
        return 1 - up;
    }

    public static double Pup(double r, double t, double u, double d){
        return (Math.exp(r*t) - d)/(u - d);
    }

    public static double down(double v, double t, double n){
        return Math.exp(-v*Math.sqrt(t/n));
    }

    public static double up(double v, double t, double n){
        return Math.exp(v*Math.sqrt(t/n));
    }

    public static int Row(int n){
        return 4*n + 2;
    }

    public static int Col(int n){
        return n + 1;
    }

    public static void PrintM(double[][] x){
        for(int i = 0; i < x.length; i++){
            for(int j = 0; j < x[0].length; j++){
                if(x[i][j] != 0){
                    System.out.print(Round(x[i][j]));
                } else {
                    System.out.print("");
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    public static double Round(double u){
        u = Math.round(u*100) / 100D;
        return u;
    }


}



package Formul;

import java.io.IOException;
import java.util.*;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

public class Formulation {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		model();
	}

	public static void model() throws IOException {
		GetData.main(null);
		int n = GetData.n; // node num
		int l = GetData.K; // vehicle num
		int[] Q = new int[l];
		List<List<Integer>> T = new ArrayList<>();

		double[][][] c = new double[n][n][l]; // cost or distance
		double[][][] w = new double[n][n][l]; // cost or distance
		double[] d = new double[n];

//		c = GetData.d; //c를 차량별 거리 설정으로 바꿔야함 나중에 
		// ****** 여기서부터 데이터 임의로 설정 ********
		double[][] tmpDis = new double[n][n];
		tmpDis = GetData.d;
		int days = 2;
		for (int i = 0; i < n; i++) {
			T.add(new ArrayList<>());
			for (int t = 1; t < days; t++) {
				T.get(i).add(t);
//				System.out.println(T.get(i).get(t-1));
			}
		}

		for (int k = 0; k < l; k++) {
			Q[k] = 10000; //
		}

		for (int j = 0; j < n; j++) {
			d[j] = 10;
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < l; k++) {
					w[i][j][k] = 10;
				}
			}
		}
		// ****** 여기까지 데이터 임의로 설정 ********
		try {
			IloCplex cplex = new IloCplex();

			// 결정변수
			IloNumVar[][][][] x = new IloNumVar[n][n][days][l];

			// 비음 제약식
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					for (int t = 1; t < days; t++) {
						if (T.get(i).contains(t) == T.get(j).contains(t)) {
							x[i][j][t] = cplex.numVarArray(l, 0, 1, IloNumVarType.Int);
						} else {
							x[i][j][t] = cplex.numVarArray(l, 0, 0, IloNumVarType.Int);
						}
					}
				}
			}

			IloNumVar[][][] u = new IloNumVar[n][days][l];
			for (int i = 0; i < n; i++) {
				for (int t = 1; t < days; t++) {
					for (int k = 0; k < l; k++) {
						if (T.get(i).contains(t))
							u[i][t][k] = cplex.numVar(0, Q[k]);
						else
							u[i][t][k] = cplex.numVar(0, 0);
					}
				}

			}

			// 목적함수
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					for (int k = 0; k < l; k++) {
						for (int t = 1; t < days; t++) {
							if (T.get(i).contains(t) == T.get(j).contains(t))
								objective.addTerm(tmpDis[i][j], x[i][j][t][k]);
						}
					}
				}
			}

			// 제약식
			IloLinearNumExpr[][][] const1 = new IloLinearNumExpr[n][days][l];
			for (int j = 1; j < n; j++) {
				for (int k = 0; k < l; k++) {
					for (int t = 1; t < days; t++) {
						const1[j][t][k] = cplex.linearNumExpr();
						for (int i = 1; i < n; i++) {
							if (T.get(i).contains(t) == T.get(j).contains(t)) {
								const1[j][t][k].addTerm(1, x[i][j][t][k]);
								const1[j][t][k].addTerm(-1, x[j][i][t][k]);
							}
						}
						cplex.addEq(const1[j][t][k], 0);
					}
				}
			}

			IloLinearNumExpr[] const2 = new IloLinearNumExpr[n];
			for (int j = 0; j < n; j++) {
				const2[j] = cplex.linearNumExpr();
				for (int t = 1; t < days; t++) {
					for (int k = 0; k < l; k++) {
						for (int i = 1; i < n; i++) {
							if (T.get(i).contains(t) == T.get(j).contains(t)) {
								const2[j].addTerm(1, x[i][j][t][k]);
							}
						}
					}
				}
				cplex.addEq(const2[j], 1);
			}

			IloLinearNumExpr[][] const3 = new IloLinearNumExpr[l][days];
			for (int k = 0; k < l; k++) {
				for (int t = 1; t < days; t++) {
					const3[k][t] = cplex.linearNumExpr();
					for (int j = 0; j < n; j++) {
						if (T.get(j).contains(t))
							const3[k][t].addTerm(1, x[0][j][t][k]);
					}
					cplex.addEq(const3[k][t], 1);
				}

			}

			IloLinearNumExpr[][][][] const4 = new IloLinearNumExpr[n][n][days][l];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					for (int t = 1; t < days; t++) {
						for (int k = 0; k < l; k++) {
							if (i != j) {
								if (T.get(i).contains(t) == T.get(j).contains(t)) {
									const4[i][j][t][k] = cplex.linearNumExpr();
									const4[i][j][t][k].addTerm(1, u[i][t][k]);
									const4[i][j][t][k].addTerm(-1, u[j][t][k]);
									const4[i][j][t][k].addTerm(Q[k], x[i][j][t][k]);
									cplex.addLe(const4[i][j][t][k], Q[k] - d[j] - w[i][j][k]);
								}
							}
						}
					}
				}
			}
			
			for(int i = 0; i < n; i++) {
				for(int k = 0; k <l; k++) {
					for(int t = 1; t<days; t++)
						cplex.addEq(x[i][i][t][k], 0);
				}
			}

			cplex.addMinimize(objective);
			cplex.setParam(IloCplex.Param.Simplex.Display, 0);
			long startTime = System.currentTimeMillis();
			cplex.solve();
			long endTime = System.currentTimeMillis();
			System.out.println("Running time : " + (endTime - startTime) / 1000. + "sec");
			System.out.println("Obj value :" + cplex.getObjValue());

			for (int t = 1; t < days; t++) {
				System.out.println(t + "일에  각 차량 이동 경로 ");
				for (int k = 0; k < l; k++) {
					int i = 0;
					int j = 0;
					
					while (j != n) {
						if (cplex.getValue(x[i][j][t][k]) > 0.5) {
							
							System.out.println((k + 1) + "Vechicle routing path : " + (i + 1) + " " + (j + 1));
							i = j;
							j = -1;
							if (i == 0)
								break;
						}
						j++;
					}
				}

			}
			cplex.end();
		} catch (IloException exc) {
			exc.printStackTrace();
		}
	}
}

package Formul;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
		
		// 공사현장i에 방문하기 시작할 수 있는 날
		int[] startDay = GetData.T;
		
		// 6월기준 30일까지 
		int endDay = 31;
		for (int i = 0; i < n; i++) {
			T.add(new ArrayList<>());
			for (int day = startDay[i]; day < endDay; day++) {
				T.get(i).add(day);
//				System.out.println(T.get(i).get(t-1));
			}
		}

		for (int k = 0; k < l; k++) {
			Q[k] = 240; //
		}
		
		d = GetData.s;
		w = GetData.w;

		// ****** 여기까지 데이터 임의로 설정 ********
		
		try {
			IloCplex cplex = new IloCplex();

			// 결정변수
			IloNumVar[][][][] x = new IloNumVar[n][n][endDay][l];

			
			
			// 비음 제약식
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					for (int t = 1; t < endDay; t++) {
						if (T.get(i).contains(t) == T.get(j).contains(t)) {
							x[i][j][t] = cplex.numVarArray(l, 0, 1, IloNumVarType.Int);
						}
						else {
							x[i][j][t] = cplex.numVarArray(l, 0, 0, IloNumVarType.Int);
						}
					}
				}
			}

			IloNumVar[][][] u = new IloNumVar[n][endDay][l];
			for (int i = 1; i < n; i++) {
				for (int t = 1; t < endDay; t++) {
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
						for (int t = 1; t < endDay; t++) {
							if (T.get(i).contains(t) == T.get(j).contains(t))
								objective.addTerm(tmpDis[i][j], x[i][j][t][k]);
						}
					}
				}
			}
			
			// TODO : T[i]값 관련 오류 해결해야함
			for (int i = 0; i < n; i++) {
				if(T.get(i).size()==0) {
					for (int j = 0; j < n; j++) {
						for (int t = 1; t < endDay; t++) {
							for(int k = 0; k < l; k++) {
								cplex.addEq(x[i][j][t][k], 0);
								cplex.addEq(x[j][i][t][k], 0);
							}
						}
					}
				}
			}
			for (int i = 0; i < n; i++) {
				if(!(T.get(i).size() == 0)){
					for (int j = 0; j < n; j++) {
						for (int t = 1; t < T.get(i).get(0); t++) {
							for(int k = 0; k < l; k++) {
								cplex.addEq(x[i][j][t][k], 0);
								cplex.addEq(x[j][i][t][k], 0);
							}
						}
					}
				}
			}

			// 제약식
			IloLinearNumExpr[][][] const1 = new IloLinearNumExpr[n][endDay][l];
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < l; k++) {
					for (int t = 1; t < endDay; t++) {
						const1[j][t][k] = cplex.linearNumExpr();
						for (int i = 0; i < n; i++) {
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
			for (int j = 1; j < n; j++) {
				const2[j] = cplex.linearNumExpr();
				for (int t = 1; t < endDay; t++) {
					for (int k = 0; k < l; k++) {
						for (int i = 0; i < n; i++) {
							if (T.get(i).contains(t) == T.get(j).contains(t)) {
								const2[j].addTerm(1, x[i][j][t][k]);
							}
						}
					}
				}
				if(T.get(j).isEmpty()) {
					cplex.addEq(const2[j], 0);
				}
				else cplex.addEq(const2[j], 1);
			}

			IloLinearNumExpr[][] const3 = new IloLinearNumExpr[l][endDay];
			for (int k = 0; k < l; k++) {
				for (int t = 1; t < endDay; t++) {
					const3[k][t] = cplex.linearNumExpr();
					for (int j = 1; j < n; j++) {
						if (T.get(j).contains(t))
							const3[k][t].addTerm(1, x[0][j][t][k]);
					}
					cplex.addLe(const3[k][t], 1);
				}

			}

			IloLinearNumExpr[][][][] const4 = new IloLinearNumExpr[n][n][endDay][l];
			for (int i = 1; i < n; i++) {
				for (int j = 1; j < n; j++) {
					for (int t = 1; t < endDay; t++) {
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
					for(int t = 1; t<endDay; t++)
						cplex.addEq(x[i][i][t][k], 0);
				}
			}

			cplex.addMinimize(objective);
			cplex.setParam(IloCplex.Param.Simplex.Display, 0);
			cplex.setParam(IloCplex.DoubleParam.TimeLimit, 3600);
			
			long startTime = System.currentTimeMillis();
//			cplex.exportModel("test.lp");
			cplex.solve();
			long endTime = System.currentTimeMillis();
			
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분");
			String nowString = now.format(dateTimeFormatter);
			
			String problemSize = "n" + n + "-k" + l + "-";
			String resultFileName = "Result-" + GetData.dataName + "-" + problemSize + nowString + ".txt";
//			String resultFileName = "Result-" + GetData.dataName + ".txt";
			
			String resultPath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\OptLab-Project\\Project\\Result\\";
			BufferedWriter bw = new BufferedWriter(new FileWriter(resultPath + resultFileName, false));
			
			System.out.println("Running time : " + (endTime - startTime) / 1000. + "sec");
			bw.write("Running time : " + (endTime - startTime) / 1000. + "sec");
			bw.newLine();
			
//			if(cplex.getStatus().equals(IloCplex.Status.Feasible) || cplex.getStatus().equals(IloCplex.Status.Optimal)) {
				System.out.println("Obj value :" + cplex.getObjValue());
				bw.write("Obj value :" + cplex.getObjValue());
				bw.newLine();
				// calculate gap rate
				double ub = cplex.getObjValue();
				double lb = cplex.getBestObjValue();
				double gap = 100 * (ub - lb) / lb;
				System.out.println("Gap : " + gap + "%");
				bw.write("Gap : " + gap + "%");
				bw.newLine();
				
				for (int t = 1; t < endDay; t++) {
					System.out.println(t + "일에  각 차량 이동 경로 ");
					bw.write(t + "일에  각 차량 이동 경로 ");
					bw.newLine();
					for (int k = 0; k < l; k++) {
						int i = 0;
						int j = 0;
						
						while (j != n) {
							if (cplex.getValue(x[i][j][t][k]) > 0.5) {
								
								System.out.println((k + 1) + "Vechicle routing path : " + (i + 1) + " " + (j + 1));
								bw.write((k + 1) + "Vechicle routing path : " + (i + 1) + " " + (j + 1));
								bw.newLine();
								i = j;
								j = -1;
								if (i == 0)
									break;
							}
							j++;
						}
					}
					
				}
//			}
//			else {
//				System.out.println("Error");
//				bw.write("Error");
//				bw.newLine();
//			}
			
			cplex.end();
			bw.close();
		} catch (IloException exc) {
			exc.printStackTrace();
		}
	}
}

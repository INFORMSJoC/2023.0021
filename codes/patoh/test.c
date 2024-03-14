#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "patoh.h"


void PrintInfo(int _k, int *partweights, int cut, int _nconst)
{
 double             *avg, *maxi, maxall=-1.0;
 int                i, j;

 printf("\n-------------------------------------------------------------------");
 printf("\n Partitioner: %s", (_nconst>1) ? "Multi-Constraint" : "Single-Constraint");

 printf("\n %d-way cutsize = %d \n", _k, cut);

 printf("\nPartWeights are:\n");
 avg = (double *) malloc(sizeof(double)*_nconst);
 maxi = (double *) malloc(sizeof(double)*_nconst);
 for (i=0; i<_nconst; ++i)
     maxi[i] = avg[i] = 0.0;
 for (i=0; i<_k; ++i)
   for (j=0; j<_nconst; ++j)
     avg[j] += partweights[i*_nconst+j];
 for (i=0; i<_nconst; ++i)
 {
     maxi[i] = 0.0;
     avg[i] /= (double) _k;
 }

 for (i=0; i<_k; ++i)
 {
     printf("\n %3d :", i);
     for (j=0; j<_nconst; ++j)
     {
         double im= (double)((double)partweights[i*_nconst+j] - avg[j]) / avg[j];

         maxi[j] = (maxi[j] > im) ? maxi[j] : im;
         printf("%10d ", partweights[i*_nconst+j]);
     }
 }
 for (j=0; j<_nconst; ++j)
     maxall = (maxi[j] > maxall) ? maxi[j] : maxall;
 printf("\n MaxImbals are (as %%): %.3lf", 100.0*maxall);
 printf("\n      ");
 for (i=0; i<_nconst; ++i)
     printf("%10.1lf ", 100.0*maxi[i]);
 printf("\n");
 free(maxi);
 free(avg);
}

void Read_Target_Weights(char *filename, float * targetweights) 

{
  size_t bufsize = 1024;
  char target_line[bufsize];
  FILE *fp;

  fp = fopen(filename, "r");
  fgets(target_line, bufsize, fp);
  fgets(target_line, bufsize, fp);
  fgets(target_line, bufsize, fp);

  char* token = strtok(target_line, " ");
  int idx = 0;
  while (token != NULL) {
    targetweights[idx++] = atof(token);
    token = strtok(NULL, " ");
}

fclose(fp);
}

void Write_Solution(char *filename, int* partvec, int c, int feasible) {
    FILE *fp;

    fp = fopen(filename, "w");

    if (feasible == 0)
        fprintf(fp, "%s\n", "Infeasible");

    for (int i = 0; i < c;i++) 
        fprintf(fp, "%d\n", partvec[i]);

    fclose(fp);
}

void Write_Results(char *filename, int *obj_vals, clock_t *execution_times, int *feasibility) {
    FILE *fp;
    fp = fopen(filename, "w");

    for (int i = 0; i < 10; i++) {
        fprintf(fp, "%d,%ld,%d\n", obj_vals[i], execution_times[i], feasibility[i]);
    }

    fclose(fp);
}



int main(int argc, char *argv[])
{

  int noDBs[] = {10, 15, 20};
  int noApps[] ={2, 3, 5};
  char* epsilons[] ={"0.1", "0.2", "0.3"};
  int ks[] = {5, 2, 3, 4, 5};
  
  char* instance_folders[] = {"node_edge_weighted" ,"edge_weighted", "node_weighted", "no_weight"};
  char * main_folder = "/home/orion/DataMitigationPlanning/journal_instances";

  //char filename[1024];
  //sprintf(filename, "%s/%s", "/home/orion/DataMitigationPlanning/journal_instances", mainFolders[0]);
  //printf("%s\n", filename);
  //char * instance_type = "node_edge_weighted"
  
  int count = 10;
  int r1 = 2;
  int r2 = 5;


  


    for (int m = 0; m < 4; m++) {
        char* sub_folder = instance_folders[m];
        char instance_folder[512];
        char weight_folder[512];
        char solution_folder[512];
        char result_folder[512];
        snprintf(instance_folder, 512, "%s/%s/%s", main_folder, sub_folder, "patoh");
        snprintf(weight_folder, 512, "%s/%s/%s", main_folder, sub_folder, "dmp");
        snprintf(solution_folder, 512, "%s/%s/%s", main_folder, sub_folder, "results/patoh_solutions");
        snprintf(result_folder, 512, "%s/%s/%s", main_folder, sub_folder, "results/patoh");

        for (int noDb = 0; noDb < 3; noDb++)  {
            int n = noDBs[noDb];
            for (int noApp = 0; noApp < 3; noApp++) {
                int m = n * noApps[noApp];
                for(int eps = 0; eps < 3; eps++) {
                    char* epsilon = epsilons[eps];
                    for (int kk = 0; kk < 4; kk++) {
                        int k = ks[kk];


                        clock_t execution_times[10];
                        int obj_vals[10];
                        int feasibility[10];
                        char patoh_result_filename[1024];

                        for (int rr = 0; rr < 10; rr++) {
                            obj_vals[rr] = -1;
                            feasibility[rr] = 0;
                            execution_times[rr] = 111111;
                        }


                        snprintf(patoh_result_filename, 1024, "%s/%d_%d_%d_%d_%d_%s.txt", result_folder, n, m, r1, r2, k, epsilon);
                        for (int instance_count = 0; instance_count < count; instance_count++){
                            char patoh_instance_filename[1024];
                            char dmp_instance_filename[1024];
                            char solution_filename[1024];
                            snprintf(dmp_instance_filename, 1024, "%s/%d_%d_%d_%d_%d_%s_%d.dmp", weight_folder, n, m, r1, r2, k, epsilon, instance_count);
                            snprintf(patoh_instance_filename, 1024, "%s/%d_%d_%d_%d_%d_%s_%d.patoh", instance_folder, n, m, r1, r2, k, epsilon, instance_count);
                            snprintf(solution_filename, 1024, "%s/%d_%d_%d_%d_%d_%s_%d.txt", solution_folder, n, m, r1, r2, k, epsilon, instance_count);

                            for (int run = 0; run < 10; run++) {
                                PaToH_Parameters args;
                                int _c, _n, _nconst, *cwghts, *nwghts, *xpins, *pins, *partvec, cut, *partweights;


                                PaToH_Read_Hypergraph(patoh_instance_filename, &_c, &_n, &_nconst, &cwghts,
                                &nwghts, &xpins, &pins);

                                PaToH_Initialize_Parameters(&args, PATOH_CONPART, PATOH_SUGPARAM_QUALITY);

                                args._k = k;
                                partvec = (int *) malloc(_c*sizeof(int));
                                partweights = (int *) malloc(args._k*_nconst*sizeof(int));
                                PaToH_Alloc(&args, _c, _n, _nconst, cwghts, nwghts, xpins, pins);


                                float* targetweights = malloc(sizeof(float) * args._k);
                                Read_Target_Weights(dmp_instance_filename, targetweights);


                                args.initp_runno = 10;
                                //args.nofinstances = 10;
                                args.crs_maxallowedcellwmult = 0.80;
                                args.initp_ghg_trybalance = 1;
                                args.initp_alg = 1;
                                args.outputdetail = 0;
                                //args.ref_alg = 7;
                                //args.ref_useafteralg = 1;
                                //args.crs_VisitOrder = 3;

                                clock_t start_time = clock() / (CLOCKS_PER_SEC / 1000);
                                PaToH_Part(&args, _c, _n, _nconst, 0, cwghts, nwghts, xpins, pins, targetweights, partvec, partweights, &cut);
                                clock_t execution_time = (clock() / (CLOCKS_PER_SEC / 1000)) - start_time;
                                
                                

                                int total_cost = 0;

                                for (int i = 0; i < _n; i++) {
                                    total_cost += (int)(nwghts[i]);
                                //printf("%f\n", nwghts[i]);
                                }

                                int total_weight = 0;

                                for (int i = 0; i < _c; i++) {
                                    total_weight += (int)(cwghts[i]);
                                //printf("%f\n", nwghts[i]);
                                }



                                int feasible = 1;
                                for (int i = 0; i < args._k; i++)
                                    if (partweights[i] > targetweights[i])
                                        feasible = 0;                

                                for (int i = 0; i < _c; i++)
                                    if (partvec[i] == -1)
                                        feasible = 0;
                                
                                //Write_Solution(solution_filename, partvec, _c, feasible);
                                int cc = PaToH_Compute_Cut(args._k, PATOH_CONPART, _c, _n, nwghts, xpins, pins, partvec);

                                if (feasible > feasibility[instance_count] || (feasible && obj_vals[instance_count] > cc + total_cost)) {
                                    feasibility[instance_count] = feasible;
                                    execution_times[instance_count] = execution_time;
                                    obj_vals[instance_count] = cc + total_cost;                                    
                                }
                                

                                //printf("Instance:%s\tConnectivity:%d\tObjective func:%d\t TW:%d\tTC:%d\n", dmp_instance_filename, cc, cc + total_cost, total_weight, total_cost);


                                //PrintInfo(args._k, partweights,  cut, _nconst);


                                free(cwghts);      free(nwghts);
                                free(xpins);       free(pins);
                                free(partweights); free(partvec);

                                PaToH_Free(); 
                            }
                        }
                        Write_Results(patoh_result_filename, obj_vals, execution_times, feasibility);
                    }
                }
            }    
        }
    }

return 0;
}

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
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
  
  //char* token = strtok(first_line," ");
  //args._k = atoi(token)
  //token = strtok(NULL, " ");
  //args._n = 
  //printf("%s\n", target_line);
  char* token = strtok(target_line, " ");
  int idx = 0;
  while (token != NULL) {
    targetweights[idx++] = atof(token);
    token = strtok(NULL, " ");
  }

  fclose(fp);

}


int main(int argc, char *argv[])
{
PaToH_Parameters args;
int             _c, _n, _nconst, *cwghts, *nwghts,
                *xpins, *pins, *partvec, cut, *partweights;





 if (argc<3) {
     fprintf(stderr, "usage: %s <filename> <#parts>\n", argv[0]);
     exit(1);
 }







PaToH_Read_Hypergraph(argv[1], &_c, &_n, &_nconst, &cwghts,
                      &nwghts, &xpins, &pins);

//printf("Hypergraph %10s -- #Cells=%6d  #Nets=%6d  #Pins=%8d #Const=%2d\n", argv[1], _c, _n, xpins[_n], _nconst);

PaToH_Initialize_Parameters(&args, PATOH_CONPART,
                            PATOH_SUGPARAM_QUALITY);

args._k = atoi(argv[2]);
partvec = (int *) malloc(_c*sizeof(int));
partweights = (int *) malloc(args._k*_nconst*sizeof(int));
PaToH_Alloc(&args, _c, _n, _nconst, cwghts, nwghts,
            xpins, pins);

char filename[256] = "./test/20_100_2_5_5_0.1_1.dmp";
float * targetweights = malloc(sizeof(float) * args._k);
Read_Target_Weights(filename, targetweights);






/* args.crs_alg = PATOH_CRS_ABS;
args.crs_useafter = 2;
  args.crs_useafteralg = PATOH_CRS_MNC;
  args.crs_coarsenper = 2; */
args.initp_runno = 10;
args.crs_maxallowedcellwmult = 0.80;
args.initp_ghg_trybalance = 0;
args.outputdetail = 1;//PATOH_OD_HIGH;

/*
PaToH_Part(&args, _c, _n, _nconst, 0, cwghts, nwghts,
           xpins, pins, NULL, partvec, partweights, &cut);

printf("%d-way cutsize is: %d\n", args._k, cut);


PrintInfo(args._k, partweights,  cut, _nconst);
*/
printf("%s\n", "Part");
PaToH_Part(&args, _c, _n, _nconst, 0, cwghts, nwghts,
           xpins, pins, targetweights, partvec, partweights, &cut);


PrintInfo(args._k, partweights,  cut, _nconst);
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



//printf("Total cost:%f\n", total_cost);

printf("%d-way cutsize is: %d\n", args._k, cut);
printf("Total cell size:%d\t Total cost:%d\n", total_weight, total_cost);
int cc = PaToH_Compute_Cut(args._k, PATOH_CONPART, _c, _n, nwghts, xpins, pins, partvec);
printf("Objective func:%d\n", cc + total_cost);


//PrintInfo(args._k, partweights,  cut, _nconst);


free(cwghts);      free(nwghts);
free(xpins);       free(pins);
free(partweights); free(partvec);

PaToH_Free();
return 0;
}

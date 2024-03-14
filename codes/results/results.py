import os
import math
import pandas as pd
import numpy as np
from scipy.stats import gmean
from result_utils import get_res_df

code_dir_abs = os.path.abspath('.')
main_dir = os.path.abspath('../../')
instance_dir = f'{main_dir}/instances'
# read the results
df, df2, dfs = get_res_df(instance_dir)

# model mapping
categories = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"]

cat2model_name = {
    'A' : '$\\langle const \\;|\\; const\\;|\\; const \\rangle$',
    'B' : '$\\langle prop \\;|\\; const\\;|\\; const \\rangle$',
    'C' : '$\\langle arb \\;|\\; const\\;|\\; const \\rangle$',
    'D' : '$\\langle const \\;|\\; arb\\;|\\; const \\rangle$',
    'E' : '$\\langle prop \\;|\\; arb\\;|\\; const \\rangle$',
    'F' : '$\\langle arb \\;|\\; arb\\;|\\; const \\rangle$',    
    'G' : '$\\langle const \\;|\\; const\\;|\\; arb \\rangle$',
    'H' : '$\\langle prop \\;|\\; const\\;|\\; arb \\rangle$',
    'I' : '$\\langle arb \\;|\\; const\\;|\\; arb \\rangle$',
    'J' : '$\\langle const \\;|\\; arb\\;|\\; arb \\rangle$',
    'K' : '$\\langle prop \\;|\\; arb\\;|\\; arb \\rangle$',
    'L' : '$\\langle arb \\;|\\; arb\\;|\\; arb \\rangle$',
}


algs = [
    'opt',
 'PaToH-Q',
 'PaToH-D',
 'KaHyPar',
 'KaHyPar-E',
 'KaHyPar-E_block']


# In[3]:


# total number of instances, total number instances after discards,
len(df), len(df2), 12960 - 7 - 13 -28 - 507


# In[4]:


#Table 1
print('Table1')
for cat in categories:
    a = df[df.type == cat]
    b = df2[df2.type == cat]
    print("\t\t\t\t{} & {}& {:.2f}& {:.2f} & {}& {:.2f}& {:.2f} & {}& {:.2f}& {:.2f} & {}& {:.2f}& {:.2f}& {}& {:.2f}& {:.2f} \\\\".format(cat2model_name[cat], 
                                            a[a['PaToH-D'] == -1].shape[0], 100*(gmean(b.test) - 1),100*(b.test.max()-1),
                                            a[a['PaToH-Q'] == -1].shape[0], 100*(gmean(b.test2) - 1),100*(b.test2.max()-1),
                                            a[a['KaHyPar'] == -1].shape[0], 100*(gmean(b.test3) - 1),100*(b.test3.max()-1),
                                            a[a['KaHyPar-E'] == -1].shape[0], 100*(gmean(b.test4) - 1),100*(b.test4.max()-1),
                                            a[a['KaHyPar-E_block'] == -1].shape[0], 100*(gmean(b.test5) - 1),100*(b.test5.max()-1)
                                                                                                          
         
         ))

print('\\midrule')  
    
print("\t\t\t\t{} & {}& {:.2f}& {:.2f} & {}& {:.2f}& {:.2f} & {}& {:.2f}& {:.2f} & {}& {:.2f}& {:.2f}& {}& {:.2f}& {:.2f} \\\\".format("All", 
                                           df[df['PaToH-D'] == -1].shape[0], 100*(gmean(df2.test) - 1),100*(df2.test.max()-1),
                                            df[df['PaToH-Q'] == -1].shape[0], 100*(gmean(df2.test2) - 1),100*(df2.test2.max()-1),
                                            df[df['KaHyPar'] == -1].shape[0], 100*(gmean(df2.test3) - 1),100*(df2.test3.max()-1),
                                            df[df['KaHyPar-E'] == -1].shape[0], 100*(gmean(df2.test4) - 1),100*(df2.test4.max()-1),
                                            df[df['KaHyPar-E_block'] == -1].shape[0], 100*(gmean(df2.test5) - 1),100*(df2.test5.max()-1)
         
         ))


# In[5]:
print('\n\nTable2')

# Table 2
models = {
    'no_test_cost' : set(['A', 'D', 'G', 'J']),
    'prop_test_cost' : set(['B', 'E', 'H', 'K']),
    'weight_test_cost' : set(['C', 'F', 'I', 'L']),
    'no_node_weight' : set(['A', 'B', 'C', 'G', 'H', 'I']),
    'node_weight' : set(['D', 'E', 'F', 'J', 'K', 'L']),
    'uniform_shift' : set(['A', 'B', 'C', 'D', 'E', 'F']),
    'non_unif_shift' : set(['G', 'H', 'I', 'J', 'K', 'L']),
    
}

m2l = {
    'no_test_cost' : '$\\langle const \\;|\\; *\\;|\\; * \\rangle$',
    'prop_test_cost' : '$\\langle prop \\;|\\; *\\;|\\; * \\rangle$',
    'weight_test_cost' : '$\\langle arb \\;|\\; *\\;|\\; * \\rangle$',
    'no_node_weight' : '$\\langle * \\;|\\; const\\;|\\; * \\rangle$',
    'node_weight' : '$\\langle * \\;|\\; arb\\;|\\; * \\rangle$',
    'uniform_shift' : '$\\langle * \\;|\\; *\\;|\\; const \\rangle$',
    'non_unif_shift' : '$\\langle * \\;|\\; *\\;|\\; arb \\rangle$',
    'All' : '$\\langle * \\;|\\; *\\;|\\; * \\rangle$'

}

for m in models:
    a = df[df.type.isin(models[m])]
    b = df2[df2.type.isin(models[m])]
    print("{} & {:.2f}& {:.2f}& {:.2f} & {:.2f}& {:.2f}& {:.2f} & {:.2f}& {:.2f}& {:.2f} & {:.2f}& {:.2f}& {:.2f}& {:.2f}& {:.2f}& {:.2f} \\\\".format(m2l[m], 
                                            100 * b[b['PaToH-D'] == b.opt].shape[0] / b.shape[0], 100*(gmean(b.test) - 1),100*(b.test.max()-1),
                                            100 * b[b['PaToH-Q'] == b.opt].shape[0]/ b.shape[0], 100*(gmean(b.test2) - 1),100*(b.test2.max()-1),
                                            100 * b[b['KaHyPar'] == b.opt].shape[0]/ b.shape[0], 100*(gmean(b.test3) - 1),100*(b.test3.max()-1),
                                            100 * b[b['KaHyPar-E'] == b.opt].shape[0]/ b.shape[0], 100*(gmean(b.test4) - 1),100*(b.test4.max()-1),
                                            100 * b[b['KaHyPar-E_block'] == b.opt].shape[0]/ b.shape[0], 100*(gmean(b.test5) - 1),100*(b.test5.max()-1)
                                                                                                          
         
         ))
    
    if m == 'weight_test_cost' or m == 'node_weight' or m == 'non_unif_shift':
        print('\\midrule')
    
print("{} & {:.2f}& {:.2f}& {:.2f} & {:.2f}& {:.2f}& {:.2f} & {:.2f}& {:.2f}& {:.2f} & {:.2f}& {:.2f}& {:.2f}& {:.2f}& {:.2f}& {:.2f} \\\\".format(m2l[m], 
                                            100 * df2[df2['PaToH-D'] == df2.opt].shape[0]/ df2.shape[0], 100*(gmean(df2.test) - 1),100*(df2.test.max()-1),
                                            100 * df2[df2['PaToH-Q'] == df2.opt].shape[0]/ df2.shape[0], 100*(gmean(df2.test2) - 1),100*(df2.test2.max()-1),
                                            100 * df2[df2['KaHyPar'] == df2.opt].shape[0]/ df2.shape[0], 100*(gmean(df2.test3) - 1),100*(df2.test3.max()-1),
                                            100 * df2[df2['KaHyPar-E'] == df2.opt].shape[0]/ df2.shape[0], 100*(gmean(df2.test4) - 1),100*(df2.test4.max()-1),
                                            100 * df2[df2['KaHyPar-E_block'] == df2.opt].shape[0]/ df2.shape[0], 100*(gmean(df2.test5) - 1),100*(df2.test5.max()-1)
                                                                                                          
         
         ))
    

print('\\bottomrule')    


# In[6]:

print('\n\n90% values')
#90% values
print(np.quantile(df2["test"].values, 0.9))
print(np.quantile(df2["test2"].values, 0.9))
print(np.quantile(df2["test3"].values, 0.9))
print(np.quantile(df2["test4"].values, 0.9))
print(np.quantile(df2["test5"].values, 0.9))

print('\n\nTable3')

#Table 3
heuristics = ['PaToH-D', 'PaToH-Q',  'KaHyPar', 'KaHyPar-E', 'KaHyPar-E_block']
for idx, h1 in enumerate(heuristics):
    res = f"{h1} & "
    for idx2, h2 in enumerate(heuristics):
        
        if idx != idx2:
            res += " {:.2f} &".format(100 * df2[df2[h1] < df2[h2]].shape[0] / df2.shape[0])    
        else:
            res += " {} &".format("-")    
#         print(h1,h2, df2[df2[h1] < df2[h2]].shape[0], df2[df2[h1] == df2[h2]].shape[0], df2[df2[h1] > df2[h2]].shape[0] )

    res = res[:-2] + " \\\\"
    print(res)


# In[8]:


import matplotlib.pyplot as plt
import matplotlib


# In[9]:


vals = [df2["test"].values,df2["test2"].values,df2["test3"].values,df2["test4"].values, df2['test5'].values]
counts = [[],[],[],[],[]]
bin_edges = []



for idx,val in enumerate(vals):
    for i in range(100, 115, 1):
        if idx == 0:
            bin_edges.append(i - 100)
            
#             print(i/200)
        counts[idx].append(val[val <= (i/100)].shape[0] / val.shape[0])
        
for idx,val in enumerate(vals):
    for i in range(115, 151, 5):
        if idx == 0:
            bin_edges.append(i- 100)
#             print(i/100)
            
        counts[idx].append(val[val <= (i/100)].shape[0] / val.shape[0])
    


# In[10]:


fig, ax  = plt.subplots(nrows=1)
plt.subplots_adjust(hspace=0.5, bottom=0.17)
# ax.plot(range(len(bin_edges)), counts[0], "r", range(len(bin_edges)), counts[1], "y", range(len(bin_edges)), counts[2], "b", range(len(bin_edges)), counts[3], "g")
ax.plot(range(len(bin_edges)), counts[4], "r", label="kKaHyPar-EBQ")
ax.plot(range(len(bin_edges)), counts[3], "g--", label="kKaHyPar-E")
ax.plot(range(len(bin_edges)), counts[0], "c:.", label="PaToH-D")
ax.plot(range(len(bin_edges)), counts[1], "y:", label="PaToH-Q")
ax.plot(range(len(bin_edges)), counts[2], "b-.", label="kKaHyPar")


ax.set_xticks(range(len(bin_edges)))
ax.set_xticklabels(bin_edges, rotation=90, horizontalalignment="right")
plt.yticks([x/100 for x in range(25,101, 5)])
# plt.xticks(bin_edges, [str(x) for x in bin_edges], rotation=90)
# plt.tick_params(axis='x', which='major', labelsize=3)
# plt.tight_layout()
plt.grid()
# plt.xscale("log")
# print(plt.xlabel())
# plt.show()
plt.legend(loc="lower right")
ax.set_xlabel("Quality relative to optimal solution")
ax.set_ylabel("Fraction of instances")
plt.savefig(f"{main_dir}/results/perf_plot.png", dpi=900)
print('\n\nFigure is generated.')

print('\n\nTable4')

#Table 4
#Running time
for cat in categories:
    a = df[df.type == cat]
    b = df2[df2.type == cat]
    print("{} &  {:.2f}& {:.2f} &  {:.2f}& {:.2f} &  {:.2f}& {:.2f} &  {:.2f}& {:.2f} \\\\".format(cat2model_name[cat],
                                            b['PaToH-D_ET'].mean(), b['PaToH-D_ET'].max(),
                                            b['PaToH-Q_ET'].mean(), b['PaToH-Q_ET'].max(),
                                            b['KaHyPar_ET'].mean(), b['KaHyPar_ET'].max(),                                                                                                          
                                            b['opt_ET'].mean(), b['opt_ET'].max(),
         
         ))
    
    
print("{} &  {:.2f}& {:.2f} &  {:.2f}& {:.2f} &  {:.2f}& {:.2f} &  {:.2f}& {:.2f} \\\\".format("All", 
                                            df2['PaToH-D_ET'].mean(), df2['PaToH-D_ET'].max(),
                                            df2['PaToH-Q_ET'].mean(), df2['PaToH-Q_ET'].max(),
                                            df2['KaHyPar_ET'].mean(), df2['KaHyPar_ET'].max(),                                                                                                          
                                            df2['opt_ET'].mean(), df2['opt_ET'].max(),
         
     ))

    


# In[ ]:





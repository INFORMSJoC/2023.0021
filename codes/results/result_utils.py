import os
import math
import pandas as pd
import numpy as np
from scipy.stats import gmean

sub_folders = ["node_edge_weighted", "node_weighted" ,"edge_weighted", "no_weight", 'node_weighted_proportional', 'no_node_weight_proportional']


def fix_name(name):
    pre = name
    vals = pre.split('_')
    vals[-2]  = f'0.{int(vals[-2])+1}'
    name_new = '_'.join(vals)

    return name_new


def compare(a,b):
    c1 = 0
    c2 = 0
    c3 = 0
    f1 = 0
    f2 = 0
    f3 = 0
    t1 = 0
    t2 = 0
    t3 = 0
    t4 = 0
    for i in range(len(a)):
        if a[i] != -1 and b[i] != -1:
            t1 += a[i]
            t2 += b[i]
            t3 += (t2/t1)
            t4 += 1
            if a[i] > b[i]:
                c2 += 1
#                 print(i, instances[i], a[i], b[i])
            elif  a[i] < b[i]:
                c1 += 1
            else:
                c3 += 1
        elif a[i] != -1:
            f1 += 1
        elif b[i] != -1:
            f2 += 1
        else:
            f3 += 1
            
    return c1,c2,c3,f1,f2,f3,t1,t2,t3/t4



def parse_instance_name(n):
    n,m,_,_,k,e,i = n[n.rfind("/")+1:].split("_")
    return n,m,k,e,i

def get_res_df(main_folder):
    #main_folder = "/home/orion/DataMitigationPlanning/journal_instances/repo/instances"
    result_folders = ["mip", "patoh_q", "patoh_d", "kahypar", "kahypar_e", "kahypar_ebq"]
    alg_names = ['opt', 'PaToH-Q', 'PaToH-D', 'KaHyPar', 'KaHyPar-E', 'KaHyPar-E_block']

    # sub_folders = ["node_edge_weighted"]
    # sub_folders = ["node_edge_weighted", "node_weighted" ,"edge_weighted", "no_weight", "no_node_weight_proportional", "node_weighted_proportional",
    #                "arbitrary_shift_sizes/node_edge_weighted", "arbitrary_shift_sizes/node_weighted" ,"arbitrary_shift_sizes/edge_weighted", "arbitrary_shift_sizes/no_weight" , "arbitrary_shift_sizes/no_node_weight_proportional", "arbitrary_shift_sizes/node_weighted_proportional"]

    sub_folders = [
        "constant_shift_sizes/no_weight", "constant_shift_sizes/no_node_weight_proportional", "constant_shift_sizes/edge_weighted",
        "constant_shift_sizes/node_weighted", "constant_shift_sizes/node_weighted_proportional", "constant_shift_sizes/node_edge_weighted",
        "arbitrary_shift_sizes/no_weight", "arbitrary_shift_sizes/no_node_weight_proportional", "arbitrary_shift_sizes/edge_weighted",
        "arbitrary_shift_sizes/node_weighted", "arbitrary_shift_sizes/node_weighted_proportional", "arbitrary_shift_sizes/node_edge_weighted",
    ]
         
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


    N = []
    M = []
    K = []
    E = []
    I = []
    all_objs = []
    all_lbs = []
    all_feasibles = []
    mip_et = []

    inf_indexes = []
    types = []
    # instances = []
    idx = 0
    dfs = []
    for alg_idx, res_folder in enumerate(result_folders):
        cidx = -1
        objs = []
        feasibility = []
        instances = []
        ets = []
        for sub_folder in sub_folders:
            result_folder = "{}/{}/results/{}".format(main_folder, sub_folder, res_folder)
            os.chdir(result_folder)
            total_feasible = 0
            total_obj = 0
            total_et = 0
            total = 0
            cidx +=1
            
            for filename in sorted(os.listdir()):
                lines = open(filename, 'r').readlines()
                if len (lines) != 10:
                    print('error:', filename)
                    for _ in range(10):
                        objs.append(-1)
                        feasibility.append(-1)
                        instances.append("-1")
                        ets.append(-1)
                        
                    continue
                
                for line in lines:
                    
                    instance_name =sub_folder+ "/"+ filename
                    instance_name = instance_name[:instance_name.rfind(".")] + "_" + str(total % 10)
                    instances.append(instance_name)
                    
                    
                    total += 1
                    idx += 1
                    res = line.replace("\n","").split(",")
    #                 if res[2] == "1" or int(res[0]) == int(float(res[2])):

                    if res_folder == "mip":
                        all_lbs.append(int(math.ceil(float(res[2]))))
                        mip_et.append(int(res[1]))
                        types.append(categories[cidx])
                    
                        n,m,k,e,i = parse_instance_name(instance_name)
                        N.append(int(n))
                        M.append(int(m))
                        K.append(int(k))
                        E.append(e)
                        I.append(int(i))

                    if res[2] != "0" and res[2] != "-1" and res[0] != 0:
                        total_feasible += 1
                        total_obj += int(res[0])
                        total_et += int(math.floor(float(res[1]) * 1000))
                        objs.append(int(res[0]))
                        feasibility.append(1)
                        ets.append(int(math.ceil(float(res[1]) )))

                  

                    else:
                        objs.append(-1)
                        feasibility.append(0)
                        ets.append(int(math.ceil(float(res[1]))))
                        
                        
        df = pd.DataFrame()
        print(alg_names[alg_idx], len(instances), len(objs))
        df['instance'] = instances
        df[alg_names[alg_idx]] = objs
        if res_folder != "mip":
            df[alg_names[alg_idx]+"_ET"] = ets 
        if res_folder == "mip":
            #"n", "m", "k", "e", "i"
            df['lb'] = all_lbs
            df['opt_ET'] = mip_et
            df['type'] = types
            df['n'] = N
            df['m'] = M
            df['k'] = K
            df['e'] = E
            df['i'] = I
            
            
    #         print("mip", df.shape)
            
            
        dfs.append(df)
            
    for zz in dfs:
        for idx, ins in enumerate(zz.instance.values):
            if '.' not in ins:
                zz.instance.values[idx] = fix_name(ins)
        
            
            
                        

        
    df2 = dfs[0]
    for df in dfs[1:]:
        df2 = pd.merge(df2, df, on="instance")

    # # df = pd.DataFrame(list(zip(instances,types, all_lbs, all_objs[0], all_objs[1] ,all_objs[2], all_objs[3], all_objs[4], all_objs[5], all_feasibles[0], all_feasibles[1], all_feasibles[2], all_feasibles[3], all_feasibles[4], mip_et, N, M, K, E, I )),
    # #                   columns=["instance","type", "LB", "opt", "PaToH-Q", "PaToH-D", "KaHyPaR", "KaHyPaR-E", "KaHyPaR-E_block", "feasible", "feasible_PaToH-Q", "feasible_PaToH-D", "feasible_KaHyPaR","feasible_KaHyPaR-E", "OPT ET", "n", "m", "k", "e", "i"])

    # discard = list(set(inf_indexes))
    # # df2 = df.drop(index=discard)
    # df2 = df

    df = df2.copy()

    print("start:", df2.shape[0])

    print("infeasible count:", df2[df2.opt == -1].shape[0])
    df2 = df2[df2['opt'] != -1]
    print(df2.shape[0])
    #calcualte not solved
    ff = []

    for idx in range(len(df2)):
        diff = abs(df2.lb.values[idx] - df2.opt.values[idx])
        if diff <= 1:
            ff.append(False)
        else:
            ff.append(True)
            

    print("couldnt solved:", df2[ff].shape[0])
    ff = []

    for idx in range(len(df2)):
        diff = abs(df2.lb.values[idx] - df2.opt.values[idx])
        if diff <= 1:
            ff.append(True)
        else:
            ff.append(False)


    df2 = df2[ff]
    print(df2.shape[0])
    last = df2.shape[0]

    df2 = df2[df2['KaHyPar'] != -1]
    df2 = df2[df2['PaToH-Q'] != -1]
    df2 = df2[df2['PaToH-D'] != -1]
    df2 = df2[df2['KaHyPar-E'] != -1]
    df2 = df2[df2['KaHyPar-E_block'] != -1]

    print("heuristic inf:", last - df2.shape[0])
    last = df2.shape[0]

    ews = []
    for inst in df2.instance.values:
        if len(inst.split("/")) == 2:
            filename = "{}/{}/dmp/{}.dmp".format(main_folder, inst.split("/")[0], inst.split("/")[1])
        if len(inst.split("/")) == 3:
            filename = "{}/{}/dmp/{}.dmp".format(main_folder, "/".join(inst.split("/")[:2]), inst.split("/")[-1])
        edge_weights = open(filename, "r").readlines()[3]
        total_ew = sum([int(w) for w in edge_weights.split()])
        ews.append(total_ew)
        
        
    df2['total_edge_weight'] = ews
    df2 = df2[df2.opt > df2.total_edge_weight]

    print("single shift:",  last - df2.shape[0])

    df2["test"] = df2['PaToH-D'].values /  df2['opt'].values
    df2["test2"] = df2['PaToH-Q'].values /  df2['opt'].values
    df2["test3"] = df2['KaHyPar'].values /  df2['opt'].values
    df2["test4"] = df2['KaHyPar-E'].values /  df2['opt'].values
    df2["test5"] = df2['KaHyPar-E_block'].values /  df2['opt'].values

    return df, df2, dfs
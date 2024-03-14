import os
import time
from tqdm import tqdm
import sys


def parse_header(header):
    s = header.split()
    return int(s[0]), int(s[1]), int(s[2])

def convert_str_to_int_list(line):
    return [int(x) for x in line.split()]

def parse_output(output, k):
    part_lines =[]
    for line in output:
        if "k-1" in line:
            obj = line.replace("\n","").strip()
        if "|part" in line:
            part_lines.append(line.replace("\n","").strip())
        if "Partition time" in line:
            et = int(1000 * float(line[line.rfind("=") + 2:line.rfind("s")].strip()))
            
    
    obj = int(obj[obj.rfind(" ") + 1:])
    parts = []
    
    for p in part_lines:
        p = int(p[p.rfind(" ") + 1:])
        parts.append(p)
        
    return obj, et, parts


sub_folders = [
    "constant_shift_sizes/node_edge_weighted",
    "constant_shift_sizes/node_weighted",
    "constant_shift_sizes/edge_weighted",
    "constant_shift_sizes/no_weight",
    'constant_shift_sizes/no_node_weight_proportional',
    'constant_shift_sizes/node_weighted_proportional', 
    "arbitrary_shift_sizes/node_edge_weighted",
    "arbitrary_shift_sizes/node_weighted",
    "arbitrary_shift_sizes/edge_weighted",
    "arbitrary_shift_sizes/no_weight",
    'arbitrary_shift_sizes/no_node_weight_proportional',
    'arbitrary_shift_sizes/node_weighted_proportional'              
              ]


main_folder = os.path.abspath("../../instances/")
config_path = os.path.abspath("./kahypar/config/km1_kKaHyPar-E_sea20.ini")
binary_path = os.path.abspath("./kahypar/build/kahypar/application/KaHyPar")



for sub_folder in sub_folders:
    print('subfolder:', sub_folder)
    obj_vals = {}
    ets = {}
    feasibility = {}
    instance_folder = f'{main_folder}/{sub_folder}/hmetis/'
    dmp_instance_folder = f'{main_folder}/{sub_folder}/dmp'
    result_folder = "{}/{}/results/kahypar_e".format(main_folder, sub_folder)

    if not os.path.exists(result_folder):
        os.mkdir(result_folder)

    os.chdir(instance_folder)
    for filename in tqdm(sorted(os.listdir()), miniters=1):
        dmp_filename = "../dmp/" + filename.replace("hmetis", "dmp")
        res_filename = filename[:filename.rfind("_")] + ".txt"
         
        f = open(dmp_filename, "r")
        lines = f.readlines()
        f.close()
        
        n,k, m = parse_header(lines[0])
        node_weights = convert_str_to_int_list(lines[1])
        target_weights = sorted(convert_str_to_int_list(lines[2]), reverse=True)
        edge_weights = convert_str_to_int_list(lines[3])
        
        tw = " ".join([str(x) for x in target_weights])
        command = """{} -h {}  -k {} -e 0 -o km1  -m direct -p {} --use-individual-part-weights 1 --part-weights {} --i-bp-early-restart 0 --i-bp-late-restart 0 --time-limit 1""".format(binary_path, instance_folder +  filename, k, config_path, tw)

        
        if sum(target_weights) < sum(node_weights):
            print(filename)
        
        feasible = 1
        feasible2 = 1
        best = 99999999
        for _ in range(1):
            stream = os.popen(command)
            output = stream.readlines()[-50:]

            try:
                obj, et, parts = parse_output(output, k)

                for idx, weight in enumerate(parts):
                    if weight > target_weights[idx]:
                        feasible = 0
    

                if feasible and obj < best:
                    best = obj

            except:
                feasible = 0
                obj = -1
                weights = [-1 for _ in range(k)]
                et = -1




            total_weight = sum(node_weights)
            for i in range(2,k):
                if sum(target_weights[:i]) >= total_weight:
                    tw = " ".join([str(x) for x in target_weights[:i]])
                    command = """{} -h {}  -k {} -e 0 -o km1  -m direct -p {} --use-individual-part-weights 1 --part-weights {} --i-bp-early-restart 0 --i-bp-late-restart 0 --time-limit 1""".format(binary_path, instance_folder +  filename, i, config_path, tw)
                    stream = os.popen(command)
                    output = stream.readlines()[-50:]
                    feasible2 = 1
                    try:
                        obj, et, parts = parse_output(output, i)

                        for idx, weight in enumerate(parts):
                            if weight > target_weights[idx]:
                                feasible2 = 0

                        if (not feasible and feasible2) or (feasible2 and obj < best):
                            best = obj
                            feasible = 1

                    except:
                        feasible2 = 0
                        continue       
            
            
        if res_filename not in obj_vals:
            obj_vals[res_filename] = []
            ets[res_filename] = []
            feasibility[res_filename] = []

        if feasible:
            obj_vals[res_filename].append(best + sum(edge_weights))
        else:
            obj_vals[res_filename].append(-1)
        ets[res_filename].append(et)
        feasibility[res_filename].append(feasible)


    for key in obj_vals:
        result_file = open("{}/{}".format(result_folder, key), "w")
        for idx in range(len(obj_vals[key])):
            result_file.write("{},{},{}\n".format(obj_vals[key][idx], ets[key][idx], feasibility[key][idx]))
            
        result_file.close()
    
    

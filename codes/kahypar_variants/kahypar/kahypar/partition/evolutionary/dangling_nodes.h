/*******************************************************************************
 * This file is part of KaHyPar.
 *
 * Copyright (C) 2017 Sebastian Schlag <uuacikalin@gmail.com>
 *
 * KaHyPar is free software: you can redistribute it and/or modify
i * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KaHyPar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KaHyPar.  If not, see <http://www.gnu.org/licenses/>.
 *
******************************************************************************/
#pragma once

#include <vector>
#include <string>
#include "kahypar/partition/evolutionary/individual.h"
#include "kahypar/utils/randomize.h"

namespace kahypar {


std::vector<int> computeDanglingNodes(Hypergraph& hg,
                       const Individual& individual) {
  std::vector<std::unordered_map<HyperedgeID, std::size_t>> blocks(hg.k());
  std::vector<std::vector<NodeID>> block2nodes(hg.k());

 
  for (const NodeID& node : hg.nodes())  {
    PartitionID block = individual.partition()[node];
    block2nodes[block].push_back(node);
    
    
    for (const HyperedgeID& he : hg.incidentEdges(node)) {
      if (blocks[block].find(he) == blocks[block].end())
        blocks[block][he] = 0;

      blocks[block][he] = blocks[block][he] + 1;
    }    
  }



  std::vector<int> clusters(hg.initialNumNodes(), -1);
  int cluster_index = -1;
  for (const std::unordered_map<HyperedgeID, std::size_t> block : blocks) {
    cluster_index++;
    std::unordered_map<HyperedgeID, bool> locked_edges;
    std::unordered_set<NodeID> locked_nodes;
    for (const auto& edge_pair : block) {
        if (hg.edgeSize(edge_pair.first) == edge_pair.second) {
          if (locked_edges.find(edge_pair.first) == locked_edges.end())
              locked_edges[edge_pair.first] = false;
  
          locked_edges[edge_pair.first] = true;          
        }
    }

    bool changed = 1;
    while (changed) {
      changed = 0;

      for (const auto& edge_pair : locked_edges) {
        if (edge_pair.second) {
          for (const NodeID& node : hg.pins(edge_pair.first)) {
            if (individual.partition()[node] != cluster_index)
              continue;

            if (locked_nodes.find(node) == locked_nodes.end()) {
              changed = 1;
              locked_nodes.insert(node);

              for (const HyperedgeID& he : hg.incidentEdges(node)) {
                if (!locked_edges[he]) {
                  changed = 1;

                  locked_edges[he] = true;
                }
              }
            }
          }
        }
        
      }
    }

    for (const auto& node : locked_nodes) 
      clusters[node] = cluster_index;
    
  }

  return clusters;  
}
}  // namespace kahypar

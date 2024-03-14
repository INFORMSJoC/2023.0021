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
#include "kahypar/partition/evolutionary/block_quality.h"

namespace kahypar {

std::vector<int> computeBlockQuality(Hypergraph& hg,
                      const Individual& individual) {
  std::vector<int> is_node_processed(hg.initialNumNodes(), 0);  
  std::vector<std::unordered_map<HyperedgeID, int>> block2edges(hg.k());
  std::vector<std::vector<NodeID>> block2nodes(hg.k());
  std::vector<std::pair<double,int>> scores(hg.k());
  std::priority_queue<std::pair<double,int>> priority_q();

  
  
  for (const NodeID& node : hg.nodes())  {
    PartitionID block = individual.partition()[node];
    block2nodes[block].push_back(node);
    
    for (const HyperedgeID& he : hg.incidentEdges(node)) {
      if (block2edges[block].find(he) == block2edges[block].end())
        block2edges[block][he] = 0;

      block2edges[block][he] = block2edges[block][he] + 1;
    }    
  }
  
  for (size_t i = 0; i < hg.k(); i++) 
    scores[i] = std::make_pair(computeBlockScore(block2edges[i], hg), i);


  std::sort(scores.begin(), scores.end(), [](auto &left, auto &right) {
    return left.first > right.first;
  });

  std::vector<int> clusters(hg.initialNumNodes(), -1);
  //pq = createPriorityQueueu(scores);

  //for (size_t i = 0; i < hg.k(); i++)
  //    priority_q.push(scores[i]);
  

  size_t last_used_block = 0;
  int num_processed_block = 0;
  
  size_t num_processed_nodes = 0;
  size_t num_total_processed_block = 0;
  
  int number_of_blocks = 2;

  if (hg.k() <= 8) {
    number_of_blocks = hg.k() / 2;
  } else{
    number_of_blocks = 3 * hg.k() / 4;
  }

  while (num_processed_block < number_of_blocks) {
      std::pair<double,int> top_block = scores[num_processed_block];
      HyperedgeID block = top_block.second;       

      for (const NodeID& node : block2nodes[block]) {
        num_processed_nodes++;
        clusters[node] = last_used_block; 
      }

      last_used_block++;
      num_processed_block++;
  }

  //std::cout << selected_block_parents << std::endl;
  return clusters;  
}
}  // namespace kahypar

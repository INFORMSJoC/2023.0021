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


double computeBlockScore(const std::unordered_map<HyperedgeID, int>& block, const Hypergraph& hg) {
  double score = 0;
  int num_edges = 0;
  for (const std::pair<HyperedgeID, int>& element : block) {
      double edge_size = hg.edgeSize(element.first);
      if (element.second > 0) {
        double partial_score = element.second / edge_size;
        score += partial_score * partial_score;
        num_edges++;
      }
  }
  return score / num_edges;
}

std::priority_queue<std::pair<double,int>> createPriorityQueueu(const std::vector<std::pair<double,int>>& scores,
                                                             const std::vector<int> is_processed) {


    std::priority_queue<std::pair<double,int>> pq;

    for (size_t i = 0; i < is_processed.size(); i++) {
          if (!is_processed[i]) {
              pq.push(scores[i]);
          }
    }

    return pq;
}

size_t findBestParentBlock(const std::vector<std::priority_queue<std::pair<double,int>>>& pqs) {
  size_t best = 0;

  for (size_t i = 1; i < pqs.size(); i++) {
    if (!pqs[i].empty() && pqs[i].top().first > pqs[best].top().first)
      best = i;
  }

  return best;
}

size_t selectBlockProportionaly(const std::vector<std::priority_queue<std::pair<double,int>>>& pqs) {
  size_t best = 0;

  for (size_t i = 1; i < pqs.size(); i++) {
    if (!pqs[i].empty() && pqs[i].top().first > pqs[best].top().first)
      best = i;
  }

  size_t tries = 0;
  while (tries <= pqs.size()) {
    tries++; 
    int random_index = Randomize::instance().getRandomInt(0, pqs.size()-1);
    if (pqs[random_index].empty())
      continue;
     
    if (Randomize::instance().getRandomFloat(0,1) < pqs[random_index].top().first / pqs[best].top().first)
      return ((size_t)(random_index));
  }

  return best;
}





std::vector<int> computeBlockQuality(Hypergraph& hg,
                      const Individuals& individuals) {

  //std::string selected_block_parents = "SBP:";
  std::vector<int> is_node_processed(hg.initialNumNodes(), 0);  
  std::vector<std::vector<std::unordered_map<HyperedgeID, int>>> parents(individuals.size());
  std::vector<std::vector<std::vector<NodeID>>> parents_block2nodes(individuals.size());
  std::vector<std::vector<size_t>> parents_block2node_count(individuals.size());
  std::vector<std::vector<int>> parents_is_block_processed(individuals.size());
  std::vector<std::vector<std::pair<double,int>>> scores(individuals.size());
  std::vector<std::priority_queue<std::pair<double,int>>> pqs(individuals.size());



  for (size_t i = 0; i < individuals.size(); i++) {
    parents[i].resize(hg.k());
    parents_block2nodes[i].resize(hg.k());
    parents_block2node_count[i].resize(hg.k(), 0);
    parents_is_block_processed[i].resize(hg.k(), 0);
    scores[i].resize(hg.k());
  }


  size_t parent_index = 0;
  for (const auto& individual : individuals) {
    for (const NodeID& node : hg.nodes())  {
      PartitionID block = individual.get().partition()[node];
      parents_block2nodes[parent_index][block].push_back(node);
      parents_block2node_count[parent_index][block]++;
      
      for (const HyperedgeID& he : hg.incidentEdges(node)) {
        if (parents[parent_index][block].find(he) == parents[parent_index][block].end())
          parents[parent_index][block][he] = 0;

        parents[parent_index][block][he] = parents[parent_index][block][he] + 1;
      }    
    }
    parent_index++;
  }

  


  for (size_t i = 0; i < individuals.size(); i++) 
    for (size_t j = 0; j < hg.k(); j++) 
      scores[i][j] = std::make_pair(computeBlockScore(parents[i][j], hg), j);
      //scores[i][j] = std::make_pair(computeBlockScore(parents[i][j]) / parents_block2node_count[i][j], j);



  std::vector<int> clusters(hg.initialNumNodes(), -1);

  for (size_t i = 0; i < individuals.size(); i++)
      pqs[i] = createPriorityQueueu(scores[i], parents_is_block_processed[i]);
  

  size_t last_used_block = 0;
  int num_processed_block = 0;
  double block_consideration = 0.75;
  size_t num_processed_nodes = 0;
  size_t num_total_processed_block = 0;
  int average_block_size = (hg.initialNumNodes() / hg.k()) + 1;


  while (num_processed_block < hg.k() * individuals.size() * block_consideration && num_processed_nodes < hg.initialNumNodes() && num_total_processed_block < hg.k() * individuals.size()) {
      num_total_processed_block++;
      size_t best_index = findBestParentBlock(pqs);
      //size_t best_index = selectBlockProportionaly(pqs);
      std::pair<double,int> top_block = pqs[best_index].top();
      pqs[best_index].pop();
      HyperedgeID block = top_block.second;
      parents_is_block_processed[best_index][block] = 1;

     //selected_block_parents = selected_block_parents +  "(" + best_index + "," + top_block + "),";
     //selected_block_parents = selected_block_parents +  "(" + std::to_string(best_index) + "," + std::to_string(block) + "," +  std::to_string(parents_block2node_count[best_index][block])  + ")---";

      for (const NodeID& node : parents_block2nodes[best_index][block]) {
        if (is_node_processed[node] == 1)
          continue;

        is_node_processed[node] = 1;
        num_processed_nodes++;
        clusters[node] = last_used_block;
        

        //update blocks of other parents
        for (size_t parent_index = 0; parent_index < parents.size(); parent_index++) {
          if (parent_index == best_index)
            continue;

          PartitionID block2 = individuals[parent_index].get().partition()[node];
          parents_block2node_count[parent_index][block2]--;
          for (const HyperedgeID& he : hg.incidentEdges(node)) 
              parents[parent_index][block2][he] = parents[parent_index][block2][he] - 1;

        }
      }


      // update priority queues
      for (size_t i = 0; i < individuals.size(); i++) {
        if (i == best_index)
            continue;

        for (size_t j = 0; j < hg.k(); j++)        
          scores[i][j] = std::make_pair(computeBlockScore(parents[i][j], hg), j);
      }

      for (size_t i = 0; i < individuals.size(); i++) {
        if (i == best_index)
          continue;

        pqs[i] = createPriorityQueueu(scores[i], parents_is_block_processed[i]);
      }


      last_used_block++;
      num_processed_block++;
  }

  //std::cout << selected_block_parents << std::endl;
  return clusters;  
}
}  // namespace kahypar

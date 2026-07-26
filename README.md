# Chess_engine #

<img width="460" height="460" alt="image" src="https://github.com/user-attachments/assets/ce3aaf0f-b7c8-4e15-889b-4133781e97a8" />

# Demo [VS 1000 ELO bot] ~6 min :  # 
Demo Video : https://drive.google.com/file/d/1_AV86W1jVaQUpVcjPmrMQ_HSGRFAbrkx/view 

Current best win : 1500 ELO
Current best draw : 1600 ELO

#   High Level Overview : #
 A visual chess interface that enables playing against a chess engine or another player \
 
 Play against a chess engine that runs a 3 move deep search operating at approximately UNKNOWN elo 

# Technical Description : # 

## Part one : Building the game of chess  ♘  ## 
 Board and pieces are rendered in a class extending JFrame, a general mouselistener listens for clicks and evaluates what the inputs are trying to move
 Inputs get sent to validity checkers that determine whether or not a move that is tried is legal -> illegal move trigger nothing, legal moves alter the board state and repaints panel
 
 
## Part two : Building an engine 🏎️ ##
 This engine uses a 2D integer matrix with alpha-beta tree pruning search variation of the minimax algorithm. Current search depth is 6
 Right now the game is being built around the player only playing white and the engine only playing black, this is to minimize overhead code in creating the engine's move responses, gameplay loop will be adapted to support both
 Performance : Currently plays at about 1500 ELO with pretty good tactical awareness but lower skill in long term strategy

# How to run for yourself: 
1. Clone this repo
2. In powershell use run.ps1/run.sh in the project directory

# Future Plans : #
 Bitboard implementation to create searches fast enough for a deeper/better engine \
 Server set up to enable web socket connections and playing against players online





 

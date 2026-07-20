#!/usr/bin/env bash
rm -f ./*.class

javac chess_board.java board_render.java chess_engine.java move_gen.java valid_move_utils.java

java chess_board

package com.aalburquerque.voronoi.struc.impl;

import java.util.ArrayList;
import java.util.List;

public class VoronoiOutput {
	
	List<Line> lines=new ArrayList<Line>();

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
	
	public void addLine(int x1, int y1, int x2, int y2){
		Line oLine=new Line();
		oLine.setX1(x1);
		oLine.setY1(y1);
		oLine.setX2(x2);
		oLine.setY2(y2);
		lines.add(oLine);
	}
	
	public void addLine(Line line){
		lines.add(line);
	}
}

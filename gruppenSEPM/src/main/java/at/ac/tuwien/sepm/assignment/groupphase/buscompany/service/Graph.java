package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import org.jgrapht.graph.DirectedMultigraph;
import org.springframework.stereotype.Component;

@Component
public class Graph {


   private DirectedMultigraph<City, Route> graph = null;

    private void create_graph(){
        if(graph == null){
            createNewGraph();
        }
    }
    public void createNewGraph(){
        graph = new DirectedMultigraph<City, Route>(Route.class);
    }

    public DirectedMultigraph<City, Route> getGraph() {
        create_graph();
        return graph;
    }

}

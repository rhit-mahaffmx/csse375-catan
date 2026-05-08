package com.catan.analysis;

import soot.*;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Soot-based Control Flow Graph generator for the Catan project.
 *
 * Analyzes compiled .class files and produces .dot (Graphviz) files
 * for each method's control flow graph.
 *
 * Usage:
 *   mvn compile exec:java -Psoot
 *
 * Render .dot files to PNG:
 *   dot -Tpng soot-cfg-output/Board_methodName.dot -o Board_methodName.png
 */
public class SootCFGGenerator {

    private static final String OUTPUT_DIR = "soot-cfg-output";

    // Classes to analyze — add more as needed
    private static final String[] TARGET_CLASSES = {
            "com.catan.domain.Board",
            "com.catan.domain.Player",
            "com.catan.domain.CityPoint",
            "com.catan.domain.TurnStateMachine"
    };

    // If non-empty, only generate CFGs for methods whose names contain one of these substrings
    private static final List<String> FOCUS_METHODS = Arrays.asList(
            "giveResourcesToBorderingSettlements",
            "onRollDiceClick",
            "placeSettlement",
            "placeRoad",
            "moveRobber",
            "tradeWithBank",
            "nextTurn",
            "endTurn",
            "buyDevelopmentCard",
            "playDevelopmentCard",
            "gatherResources",
            "redeemFishTokens",
            "distributeFishTokens"
    );

    public static void main(String[] args) {
        File outDir = new File(OUTPUT_DIR);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        boolean focusOnly = !FOCUS_METHODS.isEmpty();
        if (args.length > 0 && args[0].equals("--all")) {
            focusOnly = false;
        }

        System.out.println("=== Soot CFG Generator ===");
        System.out.println("Output directory: " + OUTPUT_DIR);
        System.out.println("Focus mode: " + (focusOnly ? "selected methods only (pass --all for everything)" : "all methods"));
        System.out.println();

        // Initialize Soot
        G.reset();

        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(Collections.singletonList("target/classes"));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_whole_program(false);
        Options.v().set_no_bodies_for_excluded(true);

        Scene.v().loadNecessaryClasses();

        CFGToDotGraph cfgDrawer = new CFGToDotGraph();

        int totalGenerated = 0;

        for (String className : TARGET_CLASSES) {
            SootClass sc;
            try {
                sc = Scene.v().getSootClass(className);
            } catch (Exception e) {
                System.err.println("Could not load class: " + className + " — " + e.getMessage());
                continue;
            }

            System.out.println("Analyzing: " + sc.getName() + " (" + sc.getMethodCount() + " methods)");

            for (SootMethod method : sc.getMethods()) {
                if (!method.isConcrete()) {
                    continue;
                }

                String methodName = method.getName();

                // Apply focus filter
                if (focusOnly && FOCUS_METHODS.stream().noneMatch(methodName::contains)) {
                    continue;
                }

                try {
                    Body body = method.retrieveActiveBody();

                    // Generate both brief and exceptional CFGs
                    // Brief CFG: ignores exception edges (cleaner view)
                    BriefUnitGraph briefCFG = new BriefUnitGraph(body);
                    DotGraph briefDot = cfgDrawer.drawCFG(briefCFG, body);
                    String sanitized = sanitize(methodName);
                    String briefFile = OUTPUT_DIR + "/" + sc.getShortName() + "_" + sanitized;
                    briefDot.plot(briefFile);

                    // Exceptional CFG: includes exception edges (complete view)
                    ExceptionalUnitGraph exceptionalCFG = new ExceptionalUnitGraph(body);
                    DotGraph exceptDot = cfgDrawer.drawCFG(exceptionalCFG, body);
                    String exceptFile = OUTPUT_DIR + "/" + sc.getShortName() + "_" + sanitized + "_exceptional";
                    exceptDot.plot(exceptFile);

                    System.out.printf("  %-45s  %3d nodes (brief)  %3d nodes (exceptional)%n",
                            methodName, briefCFG.size(), exceptionalCFG.size());
                    totalGenerated++;

                } catch (Exception e) {
                    System.err.println("  SKIPPED " + methodName + ": " + e.getMessage());
                }
            }
            System.out.println();
        }

        System.out.println("=== Complete ===");
        System.out.println("Generated " + totalGenerated + " CFG pairs (brief + exceptional) in " + OUTPUT_DIR + "/");
        System.out.println();
        System.out.println("To render as PNG images, install Graphviz and run:");
        System.out.println("  dot -Tpng soot-cfg-output/Board_giveResourcesToBorderingSettlements.dot -o Board_giveResources.png");
        System.out.println();
        System.out.println("Or render all at once:");
        System.out.println("  for %f in (soot-cfg-output\\*.dot) do dot -Tpng %f -o %~nf.png");
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}

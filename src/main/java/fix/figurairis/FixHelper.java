package fix.figurairis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Everything is done through reflection so this mod does not need Figura or Iris
 * at compile time and keeps working if their internals are slightly different.
 */
public final class FixHelper {
    public static final Logger LOG = LoggerFactory.getLogger("FiguraIrisFix");

    private static final String[] BASE_PIPELINES = {
            "ENTITY_CUTOUT", "ENTITY_CUTOUT_CULL", "ENTITY_TRANSLUCENT", "ENTITY_TRANSLUCENT_CULL"
    };

    private static boolean irisReady;
    private static boolean irisFailed;
    private static Object irisApi;
    private static Object irisProgram;
    private static Method assignMethod;
    private static int assigned;

    private FixHelper() {}

    /** Tell Iris which shader program should draw this pipeline. */
    public static synchronized void assign(Object pipeline) {
        if (pipeline == null || irisFailed) return;
        try {
            if (!irisReady) {
                Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                Class<?> programClass = Class.forName("net.irisshaders.iris.api.v0.IrisProgram");
                irisApi = apiClass.getMethod("getInstance").invoke(null);
                irisProgram = programClass.getField("ENTITIES").get(null);
                for (Method m : apiClass.getMethods()) {
                    if (m.getName().equals("assignPipeline") && m.getParameterCount() == 2) {
                        assignMethod = m;
                        break;
                    }
                }
                if (assignMethod == null) throw new NoSuchMethodException("IrisApi.assignPipeline");
                irisReady = true;
            }
            assignMethod.invoke(irisApi, pipeline, irisProgram);
            assigned++;
            LOG.info("Assigned Figura pipeline to Iris program ENTITIES ({} total): {}", assigned, pipeline);
        } catch (Throwable t) {
            irisFailed = true;
            LOG.error("Could not assign pipeline to Iris, fix disabled", t);
        }
    }

    /** Create Figura's lazily-built pipeline variants now, before Iris builds its program list. */
    public static void warmUp() {
        try {
            Class<?> vanilla = Class.forName("net.minecraft.client.renderer.RenderPipelines");
            Class<?> figura = Class.forName("org.figuramc.figura.utils.WorldRenderPipelines");
            Class<?> pipelineClass = Class.forName("com.mojang.renderpearl.api.pipeline.RenderPipeline");
            Method unlit = figura.getMethod("unlit", pipelineClass);
            Method flat = figura.getMethod("withoutPerFaceLighting", pipelineClass);
            for (String name : BASE_PIPELINES) {
                Object base = vanilla.getField(name).get(null);
                unlit.invoke(null, base);
                flat.invoke(null, base);
            }
            LOG.info("Figura pipeline variants pre-created, {} assigned to Iris", assigned);
        } catch (Throwable t) {
            LOG.error("Warm-up of Figura pipeline variants failed", t);
        }
    }
}

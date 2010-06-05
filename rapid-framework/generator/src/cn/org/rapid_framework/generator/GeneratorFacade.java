package cn.org.rapid_framework.generator;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.provider.db.DbTableFactory;
import cn.org.rapid_framework.generator.provider.db.model.Table;
import cn.org.rapid_framework.generator.provider.java.model.JavaClass;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.GeneratorException;
import cn.org.rapid_framework.generator.util.IOHelper;
/**
 * 
 * @author badqiu
 *
 */
public class GeneratorFacade {
	
	public static void printAllTableNames() throws Exception {
		List tables = DbTableFactory.getInstance().getAllTables();
		PrintUtils.printAllTableNames(tables);
	}
	
	public void generateByAllTable(String templateRootDir) throws Exception {
		List<Table> tables = DbTableFactory.getInstance().getAllTables();
		List exceptions = new ArrayList();
		for(int i = 0; i < tables.size(); i++ ) {
			try {
				generateByTable(createGenerator(templateRootDir),tables.get(i));
			}catch(GeneratorException ge) {
				exceptions.addAll(ge.getExceptions());
			}
		}
		PrintUtils.printExceptionsSumary("",exceptions);
	}
	
    public void generateByTable(String tableName,String templateRootDir) throws Exception {
		Generator g = createGenerator(templateRootDir);
		
		Table table = DbTableFactory.getInstance().getTable(tableName);
		try {
			generateByTable(g, table);
		}catch(GeneratorException ge) {
			PrintUtils.printExceptionsSumary(ge.getMessage(),ge.getExceptions());
		}
	}

	public void generateByClass(Class clazz,String templateRootDir) throws Exception {
		Generator g = createGenerator(templateRootDir);
		GeneratorModel m = GeneratorModel.newFromClass(clazz);
		PrintUtils.printBeginGenerate("JavaClass:"+clazz.getSimpleName());
		try {
			g.generateBy(m.templateModel, m.filePathModel);
		}catch(GeneratorException ge) {
			PrintUtils.printExceptionsSumary(ge.getMessage(),ge.getExceptions());
		}
	}

	public void clean() throws IOException {
		Generator g = new Generator();
		g.setOutRootDir(GeneratorProperties.getRequiredProperty("outRoot"));
		g.clean();
	}

    private void generateByTable(Generator g, Table table) throws Exception {
        GeneratorModel m = GeneratorModel.newFromTable(table);
        PrintUtils.printBeginGenerate(table.getSqlName()+" => "+table.getClassName());
        g.generateBy(m.templateModel,m.filePathModel);
    }
    
    private Generator createGenerator(String templateRootDir) {
        Generator g = new Generator();
        g.setTemplateRootDir(new File(templateRootDir).getAbsoluteFile());
        g.setOutRootDir(GeneratorProperties.getRequiredProperty("outRoot"));
        return g;
    }
	
	public static class GeneratorModel {
		public Map filePathModel;
		public Map templateModel;
		public GeneratorModel(Map templateModel, Map filePathModel) {
			super();
			this.templateModel = templateModel;
			this.filePathModel = filePathModel;
		}
		
		public static GeneratorModel newFromTable(Table table) {
			Map templateModel = new HashMap();
			templateModel.putAll(GeneratorProperties.getProperties());
			templateModel.put("table", table);
			setShareVars(templateModel);
			
			Map filePathModel = new HashMap();
			filePathModel.putAll(GeneratorProperties.getProperties());
			filePathModel.putAll(BeanHelper.describe(table));
			return new GeneratorModel(templateModel,filePathModel);
		}

		public static GeneratorModel newFromClass(Class clazz) {
			Map templateModel = new HashMap();
			templateModel.putAll(GeneratorProperties.getProperties());
			templateModel.put("clazz", new JavaClass(clazz));
			setShareVars(templateModel);
			
			Map filePathModel = new HashMap();
			filePathModel.putAll(GeneratorProperties.getProperties());
			filePathModel.putAll(BeanHelper.describe(new JavaClass(clazz)));
			return new GeneratorModel(templateModel,filePathModel);
		}
		
		private static void setShareVars(Map templateModel) {
			templateModel.putAll(System.getProperties());
			templateModel.put("env", System.getenv());
		}
	}
	
	private static class PrintUtils {
		
		private static void printExceptionsSumary(String msg,List<Exception> exceptions) {
			File errorFile = new File(GeneratorProperties.getRequiredProperty("outRoot"),"generator_error.log");
			if(exceptions != null && exceptions.size() > 0) {
				System.err.println("[Generate Error Summary] : "+msg);
				ByteArrayOutputStream errorLog = new ByteArrayOutputStream();
				for(Exception e : exceptions) {
					System.err.println("[GENERATE ERROR]:"+e);
					e.printStackTrace(new PrintStream(errorLog));
				}
				IOHelper.saveFile(errorFile, errorLog.toString());
				System.err.println("***************************************************************");
				System.err.println("* "+"* 输出目录已经生成generator_error.log用于查看错误 ");
				System.err.println("***************************************************************");
			}
		}
		
		private static void printBeginGenerate(String displayText) {
			GLogger.println("***************************************************************");
			GLogger.println("* BEGIN generate " + displayText);
			GLogger.println("***************************************************************");
		}
		
		public static void printAllTableNames(List<Table> tables) throws Exception {
			GLogger.println("\n----All TableNames BEGIN----");
			for(int i = 0; i < tables.size(); i++ ) {
				String sqlName = ((Table)tables.get(i)).getSqlName();
				GLogger.println("g.generateTable(\""+sqlName+"\");");
			}
			GLogger.println("----All TableNames END----");
		}
	}
}

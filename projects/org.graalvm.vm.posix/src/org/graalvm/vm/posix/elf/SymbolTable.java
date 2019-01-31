package org.graalvm.vm.posix.elf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable extends Section {
	private List<Symbol> symbols;
	private Map<String, Symbol> globals;

	public SymbolTable(Section section) {
		super(section);

		if((section.sh_size % section.sh_entsize) != 0) {
			throw new IllegalArgumentException("invalid section");
		}

		int cnt = (int) (section.sh_size / section.sh_entsize);

		symbols = new ArrayList<>();
		globals = new HashMap<>();
		for(int i = 0; i < cnt; i++) {
			Symbol sym = new Symbol(section, i);
			symbols.add(sym);
			if(sym.getVisibility() == Symbol.DEFAULT) {
				if(sym.getBind() == Symbol.GLOBAL) {
					globals.put(sym.getName(), sym);
				} else if(sym.getBind() == Symbol.WEAK && !globals.containsKey(sym.getName())) {
					globals.put(sym.getName(), sym);
				}
			}
		}
	}

	public Symbol getSymbol(int i) {
		return symbols.get(i);
	}

	public Symbol getSymbol(String name) {
		return globals.get(name);
	}

	public List<Symbol> getSymbols() {
		return Collections.unmodifiableList(symbols);
	}

	public int size() {
		return symbols.size();
	}
}

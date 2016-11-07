
public enum ChessType{
		EMPTY, 
		BLACK, 
		WHITE;
	
		public static ChessType nextType(ChessType type){
			if(type == ChessType.BLACK){
				return ChessType.WHITE;
			}
			else if(type  == ChessType.WHITE){
				return ChessType.BLACK;
			}
			else {
				return ChessType.EMPTY;
			}
		}
		
		public static int getChessTypeNo(ChessType type){
			return type.ordinal();
		}
		
}
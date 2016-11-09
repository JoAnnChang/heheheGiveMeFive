
public enum ChessType{
		EMPTY, 
		BLACK, 
		WHITE;
	
	    /**
	     * Return the next ChessType, for example,
	     * pass BLACK -> get WHITE
	     * pass WHITE -> get BLACK
	     * However, pass EMPTY still get EMPTY
	     *
	     * @return ChessType : the next type
	     */
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
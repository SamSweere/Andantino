package game;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.pow;

public class TT {
    //q, r, s
    private final long[][][] zobrist;
    private final int hashKeyBytes = 3;
    private final int hashKeyBits = hashKeyBytes*8;
    private TTElement[] tt;
    private long initHashKey;
    public int collisionCounter = 0;




    //fast way to convert long to bytes and back
    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < b.length; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    //Return the index if found, otherwise add it to the table and return -1
    public TTElement checkTT(long hash){
        //Most efficient way I could find to split long
        byte[] hashBytes = longToBytes(hash);

        //Long is 64 bits thus 8 bytes
        byte[] hashKeyByte = new byte[hashKeyBytes];
        byte[] primaryHashByte = new byte[8-hashKeyBytes];

        //Copy the hash into the two keys
        for(int i = 0; i < 8; i++){
            if(i < hashKeyBytes){
                hashKeyByte[i] = hashBytes[i];
            }else{
                primaryHashByte[i-hashKeyBytes] = hashBytes[i];
            }
        }

        //Convert them back to Long's
        long primaryHash = bytesToLong(primaryHashByte);
        int hashKey = (int)bytesToLong(hashKeyByte);

        //Check if it is present in the tt
        TTElement contents = tt[hashKey];
        if(contents == null){
            //Element is empty, add it
            //tt[hashKey] = new TTElement(primaryHash, value, depth);

            //Not found in tt, return -1
            return null;
        }else{
            //this hashKey is not emtpy, check for collision
            if(contents.primaryHash == primaryHash){
                //Collision occured
                return null;
            }
            else{
                //No collision, return the found element

                //TODO:this disables the TT
                //return null;
                return contents;
            }
        }
    }

    public void storeTT(long hash, int flag, int value, int depth, Move move){
        //Most efficient way I could find to split long
        byte[] hashBytes = longToBytes(hash);

        //Long is 64 bits thus 8 bytes
        byte[] hashKeyByte = new byte[hashKeyBytes];
        byte[] primaryHashByte = new byte[8-hashKeyBytes];

        //Copy the hash into the two keys
        for(int i = 0; i < 8; i++){
            if(i < 8-hashKeyBytes){
                primaryHashByte[i] = hashBytes[i];
            }else{
                hashKeyByte[i-(8-hashKeyBytes)] = hashBytes[i];
            }
        }

        //Convert them back to Long's
        long primaryHash = bytesToLong(primaryHashByte);
        int hashKey = (int)bytesToLong(hashKeyByte);

        //Make the tt element
        TTElement ttElem = new TTElement(primaryHash, flag, value, depth, move);

        //Check if it is present in the tt
        TTElement contents = tt[hashKey];
        if(contents == null){
            //Element is empty, add it
            tt[hashKey] = ttElem;
        }else{
            //this hashKey is not emtpy, check for collision
            if(contents.primaryHash == primaryHash){
                //Collision occured
                //TODO: replacement strategy is here new, therefore do not return this value
                collisionCounter += 1;
                tt[hashKey] = ttElem;
            }
            else{
                //No collision, this one is already taken, you should not be here
            }
        }
    }

    public TT(int boardRadius){
        //Create the random values for every parameter
        zobrist = new long[boardRadius*2+1][boardRadius*2+1][5];

        //Fill them with random long values
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                for(int s = 0; s < 5; s++){
                    zobrist[q][r][s] = ThreadLocalRandom.current().nextLong();
                }
            }
        }

        //Create the transposition table with size 2^hashKeyBits
        this.tt = new TTElement[(int)pow(2,hashKeyBits)];
    }

    //Calculates the hash of a board
    public long getHash(Board board) {
        long hashKey = 0;
        int boardRadius = board.getBoardRadius();
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    //The state has to be translated to the good index
                    if(board.getTileState(q, r) == 1){
                        hashKey ^= zobrist[q][r][1];
                    }else if(board.getTileState(q, r) == -1){
                        hashKey ^= zobrist[q][r][2];
                    }else if(board.getTileState(q, r) == -2){
                        hashKey ^= zobrist[q][r][3];
                    }else if(board.getTileState(q, r) == 2){
                        hashKey ^= zobrist[q][r][4];
                    }
                    else{
                        hashKey ^= zobrist[q][r][0];
                    }

                }
            }
        }
        return hashKey;
    }

    public long updateHash(long hashKey, Move move, int player) {
        //Make sure the playernumber is converted to the array acces value
        int playerNum = 0;
        if(player == 0){
            playerNum = 1;
        }
        else if(player == -1){
            playerNum = 2;
        }
        hashKey ^= zobrist[move.q][move.r][playerNum];
        return hashKey;
    }


}

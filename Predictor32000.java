
public class Predictor32000 extends Predictor {

	Table first,second;
	Register r;
	int bits=10;
	int width=12;

	public Predictor32000() {
		int row1=(int)Math.pow(2,bits);
		int row2=(int)Math.pow(2,width);
		first=new Table(row1,width);	//first is the table of BHRs
		second=new Table(row2,2);		//second is the table of actual saturating counters
		r=new Register(bits);
	}

	public void Train(long address, boolean outcome, boolean predict) {
		int index=0;	//this is the index in first table
		int count=0;
		while(count<bits){
			long x=address%2;
			address=address/2;
			int pow=1;
			for(int i=0;i<count;i++){
				pow=pow*2;
			}
			index=index+(int)x*pow;
			count++;
		}
		count=0;
		int index2=0;		//index in the second table
		while(count<width){
			int d=first.getBit(index,count)==true?1:0;
			int pow=1;
			for(int i=0;i<count;i++){
				pow=pow*2;
			}
			index2=index2+pow*d;
			count++;
		}
		boolean a=second.getBit(index2,1);
		boolean b=second.getBit(index2,0);
		if(a==false && b==false){
			if(outcome==true){
				a=false;
				b=true;
			}
		}
		else if(a==false && b==true){
			if(outcome==true){
				a=true;
				b=false;
			}
			else{
				a=false;
				b=false;
			}
		}
		else if(a==true && b==false){
			if(outcome==true){
				a=true;
				b=true;
			}
			else{
				a=false;
				b=true;
			}
		}
		else {
			if(outcome==true){
				a=true;
				b=true;
			}
			else{
				a=true;
				b=false;
			}
		}
		second.setBit(index2,0,b);
		second.setBit(index2,1,a);

		for(int i=0;i<bits-1;i++){
			boolean bitToSet=r.getBitAtIndex(i+1);
			r.setBitAtIndex(i,r.getBitAtIndex(i+1));
		}
		r.setBitAtIndex(bits-1,outcome);


		for(int i=0;i<width-1;i++){
			first.setBit(index,i,first.getBit(index,i+1));
		}
		
		first.setBit(index,width-1,outcome);
	}

	public boolean predict(long address){
		int index=0;		//this is the index in first table
		int count=0;
		while(count<bits){
			long x=address%2;
			address=address/2;
			/*
			int y=r.getBitAtIndex(count)==true?1:0;
			int xor=0;
			if(y!=(int)(x)){
				xor=1;
			}
			*/
			int pow=1;
			for(int i=0;i<count;i++){
				pow=pow*2;
			}
			index=index+(int)x*pow;
			count++;
		}
		count=0;
		int index2=0;		//index in the second table
		while(count<width){
			int d=first.getBit(index,count)==true?1:0;
			int pow=1;
			for(int i=0;i<count;i++){
				pow=pow*2;
			}
			index2=index2+pow*d;
			count++;
		}
		boolean a=second.getBit(index2,1);
		boolean b=second.getBit(index2,0);
		boolean result=(a==true && b==true) || (a==true && b==false);
		return result;
	}

}
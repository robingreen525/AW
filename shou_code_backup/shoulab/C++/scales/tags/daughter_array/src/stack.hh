#ifndef ADT_STACK_HH
#define ADT_STACK_HH
#include <iostream>
#include <cstdlib>
#include <new>

template <typename Item>
class Stack
{
    private:
        struct node
        {
            Item item; node* next;
            node(Item x, node* t) { item = x; next = t; }
        };
        typedef node * link;
        link head;
        unsigned int items;
    public:
        Stack() { head = 0; items = 0; }
        ~Stack();
        int empty() const { return head == 0; }
        void push (Item x); 
        Item pop();
        const Item & view_top() { return head->item; }
        unsigned int count() { return items; }
};
#endif

template <typename Item>
void Stack<Item>::push(Item x) {
  try {
    head = new node(x, head);
  }
  catch (std::bad_alloc & ba) {
    std::cout << "Stack out of room!\n";
    std::cout << ba.what() << std::endl;
    exit(EXIT_FAILURE);
  }
  items++;
} 

template <typename Item>
Stack<Item>::~Stack() {
  node * temp; 
  while ( head !=0 ) { 
    temp = head;
    head = head->next;
    delete temp;
  };
}

template <typename Item>
Item Stack<Item>::pop() {
  Item v = head->item; 
  link t = head->next;
  delete head; 
  head = t;
  items--;
  return v;
}
